package pub.module.common.plugin.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.annotation.ImportCandidates;
import pub.module.common.plugin.model.PluginInstallRecord;
import pub.module.common.plugin.model.PluginLoadState;
import pub.module.common.plugin.registry.PluginInstallRegistry;
import pub.module.common.plugin.spi.TgPlugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 从配置目录扫描 *.jar，注册其中的 Spring Boot AutoConfiguration，并记录安装状态。
 */
@Slf4j
public final class ExternalPluginBootstrap {

    private static volatile URLClassLoader pluginClassLoader;
    private static volatile List<Class<?>> cachedExtraPrimarySources;

    private ExternalPluginBootstrap() {
    }

    /**
     * 供宿主应用持有的插件 ClassLoader，避免被 GC 回收。
     */
    public static URLClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    /**
     * 解析插件目录并装载可识别的 AutoConfiguration 类，供 {@link org.springframework.boot.SpringApplication} 作为 primarySources。
     */
    public static List<Class<?>> resolveExtraPrimarySources(String[] args) {
        List<Class<?>> cached = cachedExtraPrimarySources;
        if (cached != null) {
            return cached;
        }
        synchronized (ExternalPluginBootstrap.class) {
            if (cachedExtraPrimarySources != null) {
                return cachedExtraPrimarySources;
            }
            List<Class<?>> resolved = doResolveExtraPrimarySources(args);
            cachedExtraPrimarySources = List.copyOf(resolved);
            return cachedExtraPrimarySources;
        }
    }

    private static List<Class<?>> doResolveExtraPrimarySources(String[] args) {
        String dir = resolveDirectory(args);
        Path pluginDir = Paths.get(dir).toAbsolutePath().normalize();
        List<PluginInstallRecord> records = new ArrayList<>();

        if (!Files.isDirectory(pluginDir)) {
            log.info("插件目录不存在或未创建，已跳过外部插件装载: {}", pluginDir);
            PluginInstallRegistry.replaceAll(List.of());
            return List.of();
        }

        List<Path> jars;
        try (Stream<Path> stream = Files.list(pluginDir)) {
            jars = stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".jar"))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.warn("列出插件目录失败: {}", pluginDir, e);
            records.add(failedJarRecord(pluginDir.toString(), e));
            PluginInstallRegistry.replaceAll(records);
            return List.of();
        }

        ClassLoader parent = ExternalPluginBootstrap.class.getClassLoader();
        List<URL> mergedUrls = new ArrayList<>();
        Set<String> allConfigClassNames = new LinkedHashSet<>();

        for (Path jar : jars) {
            PluginInstallRecord rec = inspectJar(jar, parent, mergedUrls, allConfigClassNames);
            records.add(rec);
        }

        PluginInstallRegistry.replaceAll(records);

        if (mergedUrls.isEmpty() || allConfigClassNames.isEmpty()) {
            return List.of();
        }

        URLClassLoader merged = URLClassLoader.newInstance(mergedUrls.toArray(URL[]::new), parent);
        pluginClassLoader = merged;

        List<Class<?>> classes = new ArrayList<>();
        for (String name : allConfigClassNames) {
            try {
                classes.add(Class.forName(name, false, merged));
            } catch (ClassNotFoundException e) {
                log.error("无法加载插件 AutoConfiguration: {}", name, e);
                augmentFailureRecord(records, name, e);
            }
        }
        PluginInstallRegistry.replaceAll(records);
        return classes;
    }

    private static void augmentFailureRecord(List<PluginInstallRecord> records, String className, Exception e) {
        for (PluginInstallRecord r : records) {
            if (r.getAutoConfigurationClasses() != null && r.getAutoConfigurationClasses().contains(className)) {
                r.setLoadState(PluginLoadState.FAILED);
                r.setMessage(shorten(e));
            }
        }
    }

    private static PluginInstallRecord inspectJar(Path jar, ClassLoader parent, List<URL> mergedUrls,
                                                  Set<String> allConfigClassNames) {
        String fileName = jar.getFileName().toString();
        URL url;
        try {
            url = jar.toUri().toURL();
        } catch (MalformedURLException e) {
            return PluginInstallRecord.builder()
                    .jarFileName(fileName)
                    .pluginCode("?")
                    .pluginName(fileName)
                    .pluginDescription("")
                    .loadState(PluginLoadState.FAILED)
                    .message(shorten(e))
                    .build();
        }

        try (URLClassLoader one = URLClassLoader.newInstance(new URL[]{url}, parent)) {
            TgPlugin meta = firstSpi(one);
            String code = meta != null ? meta.getPluginCode() : deriveCode(fileName);
            String pname = meta != null ? meta.getPluginName() : fileName;
            String pdesc = meta != null ? meta.getPluginDescription() : "";

            List<String> candidates = ImportCandidates.load(AutoConfiguration.class, one).getCandidates();
            if (candidates.isEmpty()) {
                return PluginInstallRecord.builder()
                        .jarFileName(fileName)
                        .pluginCode(code)
                        .pluginName(pname)
                        .pluginDescription(pdesc)
                        .loadState(PluginLoadState.NO_AUTO_CONFIGURATION)
                        .message("未发现 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports")
                        .build();
            }

            for (String cn : candidates) {
                Class.forName(cn, false, one);
            }

            mergedUrls.add(url);
            allConfigClassNames.addAll(candidates);

            return PluginInstallRecord.builder()
                    .jarFileName(fileName)
                    .pluginCode(code)
                    .pluginName(pname)
                    .pluginDescription(pdesc)
                    .loadState(PluginLoadState.LOADED)
                    .message("已并入 Spring 启动源")
                    .autoConfigurationClasses(String.join(",", candidates))
                    .build();
        } catch (Throwable t) {
            log.warn("插件 JAR 处理失败: {}", fileName, t);
            return PluginInstallRecord.builder()
                    .jarFileName(fileName)
                    .pluginCode("?")
                    .pluginName(fileName)
                    .pluginDescription("")
                    .loadState(PluginLoadState.FAILED)
                    .message(shorten(t))
                    .build();
        }
    }

    private static PluginInstallRecord failedJarRecord(String path, Exception e) {
        return PluginInstallRecord.builder()
                .jarFileName(path)
                .pluginCode("?")
                .pluginName(path)
                .pluginDescription("")
                .loadState(PluginLoadState.FAILED)
                .message(shorten(e))
                .build();
    }

    private static String shorten(Throwable t) {
        String m = t.getMessage();
        if (m == null) {
            m = t.getClass().getSimpleName();
        }
        return m.length() > 500 ? m.substring(0, 500) + "…" : m;
    }

    private static String deriveCode(String fileName) {
        String base = fileName.endsWith(".jar") ? fileName.substring(0, fileName.length() - 4) : fileName;
        return base.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private static TgPlugin firstSpi(URLClassLoader cl) {
        ServiceLoader<TgPlugin> loader = ServiceLoader.load(TgPlugin.class, cl);
        Iterator<TgPlugin> it = loader.iterator();
        return it.hasNext() ? it.next() : null;
    }

    /**
     * 优先顺序：命令行 --tg.plugins.directory= → 系统属性 tg.plugins.directory → 环境变量 TG_PLUGINS_DIRECTORY → ./plugins
     */
    public static String resolveDirectory(String[] args) {
        if (args != null) {
            for (String a : args) {
                if (a != null && a.startsWith("--tg.plugins.directory=")) {
                    return a.substring("--tg.plugins.directory=".length()).trim();
                }
            }
        }
        String prop = System.getProperty("tg.plugins.directory");
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }
        String env = System.getenv("TG_PLUGINS_DIRECTORY");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String fromYaml = readPluginsDirectoryFromYaml();
        if (fromYaml != null && !fromYaml.isBlank()) {
            return fromYaml.trim();
        }
        return "./plugins";
    }

    @SuppressWarnings("unchecked")
    private static String readPluginsDirectoryFromYaml() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ExternalPluginBootstrap.class.getClassLoader();
        }
        Map<String, Object> root = readYamlRoot(cl, "application.yml");
        String fromMain = root != null ? extractTgPluginsDirectory(root) : null;
        if (fromMain != null) {
            return fromMain;
        }
        String profile = System.getProperty("spring.profiles.active");
        if (profile == null || profile.isBlank()) {
            profile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        if (profile == null || profile.isBlank()) {
            profile = root != null ? extractSpringProfile(root) : null;
        }
        if (profile != null && !profile.isBlank()) {
            String first = profile.split(",")[0].trim();
            Map<String, Object> profRoot = readYamlRoot(cl, "application-" + first + ".yml");
            if (profRoot != null) {
                return extractTgPluginsDirectory(profRoot);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> readYamlRoot(ClassLoader cl, String classpathYaml) {
        try (InputStream in = cl.getResourceAsStream(classpathYaml)) {
            if (in == null) {
                return null;
            }
            Yaml yaml = new Yaml();
            Object root = yaml.load(in);
            if (root instanceof Map) {
                return (Map<String, Object>) root;
            }
        } catch (Exception e) {
            log.debug("读取 {} 失败: {}", classpathYaml, e.getMessage());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String extractSpringProfile(Map<String, Object> root) {
        Object spring = root.get("spring");
        if (!(spring instanceof Map)) {
            return null;
        }
        Object profiles = ((Map<String, Object>) spring).get("profiles");
        if (!(profiles instanceof Map)) {
            return null;
        }
        Object active = ((Map<String, Object>) profiles).get("active");
        return active != null ? active.toString().trim() : null;
    }

    @SuppressWarnings("unchecked")
    private static String extractTgPluginsDirectory(Map<String, Object> root) {
        Object tg = root.get("tg");
        if (!(tg instanceof Map)) {
            return null;
        }
        Object plugins = ((Map<String, Object>) tg).get("plugins");
        if (!(plugins instanceof Map)) {
            return null;
        }
        Object dir = ((Map<String, Object>) plugins).get("directory");
        return dir != null ? dir.toString().trim() : null;
    }
}
