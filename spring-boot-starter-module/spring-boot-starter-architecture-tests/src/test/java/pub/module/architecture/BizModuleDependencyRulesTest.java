package pub.module.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 禁止 {@code *-biz} 模块的 pom 依赖其他域的 {@code *-biz}（仅允许依赖 {@code *-api} 与 common）。
 */
class BizModuleDependencyRulesTest {

    private static final Pattern BIZ_POM = Pattern.compile("spring-boot-starter-[\\w-]+-biz/pom\\.xml$");
    private static final Pattern ARTIFACT_ID = Pattern.compile("<artifactId>([^<]+)</artifactId>");
    private static final Pattern BIZ_ARTIFACT = Pattern.compile("spring-boot-starter-[\\w-]+-biz");

    @Test
    void bizModulesMustNotDependOnOtherBizModules() throws IOException {
        Path moduleRoot = ModulePaths.locateSpringBootStarterModuleRoot();
        List<String> violations = new ArrayList<>();
        try (Stream<Path> poms = Files.walk(moduleRoot)) {
            poms.filter(path -> BIZ_POM.matcher(moduleRoot.relativize(path).toString().replace('\\', '/')).find())
                    .forEach(pom -> collectViolations(pom, violations));
        }
        assertTrue(violations.isEmpty(), "发现 *-biz 依赖其他 *-biz:\n" + String.join("\n", violations));
    }

    private static void collectViolations(Path pomPath, List<String> violations) {
        try {
            String content = Files.readString(pomPath);
            String selfArtifact = pomPath.getParent().getFileName().toString();
            String dependenciesBlock = extractDependenciesBlock(content);
            Matcher matcher = ARTIFACT_ID.matcher(dependenciesBlock);
            while (matcher.find()) {
                String artifactId = matcher.group(1);
                if (BIZ_ARTIFACT.matcher(artifactId).matches() && !artifactId.equals(selfArtifact)) {
                    violations.add(pomPath + " -> " + artifactId);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("读取 pom 失败: " + pomPath, ex);
        }
    }

    private static String extractDependenciesBlock(String content) {
        int start = content.indexOf("<dependencies>");
        if (start < 0) {
            return "";
        }
        int end = content.indexOf("</dependencies>", start);
        if (end < 0) {
            return "";
        }
        return content.substring(start, end);
    }
}
