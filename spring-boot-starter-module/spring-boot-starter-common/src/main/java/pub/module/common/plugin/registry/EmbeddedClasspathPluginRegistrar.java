package pub.module.common.plugin.registry;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pub.module.common.plugin.model.PluginInstallRecord;
import pub.module.common.plugin.model.PluginLoadState;
import pub.module.common.plugin.spi.TgPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 将随 Runner 一并打包的 Maven 依赖中的 {@link TgPlugin} SPI 登记到内存（与 ./plugins 外来 JAR 并列展示）。
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class EmbeddedClasspathPluginRegistrar implements ApplicationListener<ApplicationReadyEvent> {

    private static final Pattern JAR_TAIL = Pattern.compile("([^/!]+\\.jar)");
    private static final Pattern BOOT_INF_LIB_JAR = Pattern.compile("BOOT-INF/lib/([^!?]+\\.jar)");
    private static final String AUTO_CONFIGURATION_IMPORTS =
            "/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ClassLoader cl = event.getApplicationContext().getClassLoader();
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        List<PluginInstallRecord> found = new ArrayList<>();
        ServiceLoader<TgPlugin> loader = ServiceLoader.load(TgPlugin.class, cl);
        for (TgPlugin p : loader) {
            Class<?> impl = p.getClass();
            String imports = readAutoConfigurationImports(impl);
            String jar = jarNameForClass(impl);
            found.add(PluginInstallRecord.builder()
                    .jarFileName(jar)
                    .pluginCode(p.getPluginCode())
                    .pluginName(p.getPluginName())
                    .pluginDescription(p.getPluginDescription())
                    .loadState(PluginLoadState.LOADED)
                    .message("已作为 Runner Maven 依赖（classpath）加载")
                    .autoConfigurationClasses(imports)
                    .build());
        }
        PluginInstallRegistry.addAllIfAbsentByPluginCode(found);
    }

    /**
     * 读取与 SPI 实现类同 JAR（同一条 classpath 资源）下的 Boot 3 AutoConfiguration 清单。
     */
    static String readAutoConfigurationImports(Class<?> implClass) {
        URL url = implClass.getResource(AUTO_CONFIGURATION_IMPORTS);
        if (url == null) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String collected = br.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.joining(","));
            return collected.isEmpty() ? null : collected;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从资源 URL 推断 lib 名（fat JAR 嵌套 / 普通 jar 皆可尝试）。
     */
    static String jarNameFromImportsResourceUrl(String resourceUrl) {
        if (resourceUrl == null || resourceUrl.isBlank()) {
            return null;
        }
        Matcher boot = BOOT_INF_LIB_JAR.matcher(resourceUrl);
        if (boot.find()) {
            return boot.group(1);
        }
        Matcher m = JAR_TAIL.matcher(resourceUrl);
        String last = null;
        while (m.find()) {
            last = m.group(1);
        }
        return last;
    }

    /**
     * 适配 file: 单 JAR 以及 Spring Boot fat JAR 下的 nested: / jar:nested: 等 location 形态。
     */
    static String jarNameForClass(Class<?> c) {
        try {
            URL importsResource = c.getResource(AUTO_CONFIGURATION_IMPORTS);
            if (importsResource != null) {
                String fromImports = jarNameFromImportsResourceUrl(importsResource.toString());
                if (fromImports != null && !fromImports.isBlank()) {
                    return fromImports;
                }
            }
            ProtectionDomain pd = c.getProtectionDomain();
            if (pd == null) {
                return implFallbackName(c);
            }
            CodeSource cs = pd.getCodeSource();
            if (cs == null || cs.getLocation() == null) {
                return implFallbackName(c);
            }
            URL loc = cs.getLocation();
            String s = loc.toString();
            if (s.contains("BOOT-INF/lib/")) {
                int i = s.indexOf("BOOT-INF/lib/");
                String tail = s.substring(i + "BOOT-INF/lib/".length());
                int cut = tail.indexOf('!');
                if (cut > 0) {
                    tail = tail.substring(0, cut);
                }
                cut = tail.indexOf('/');
                if (cut > 0) {
                    tail = tail.substring(0, cut);
                }
                return tail.isEmpty() ? implFallbackName(c) : tail;
            }
            String last = null;
            Matcher m = JAR_TAIL.matcher(s);
            while (m.find()) {
                last = m.group(1);
            }
            return last != null ? last : implFallbackName(c);
        } catch (Exception e) {
            return implFallbackName(c);
        }
    }

    private static String implFallbackName(Class<?> c) {
        return c.getSimpleName() + ".class";
    }
}
