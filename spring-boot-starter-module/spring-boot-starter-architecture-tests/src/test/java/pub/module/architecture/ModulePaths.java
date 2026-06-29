package pub.module.architecture;

import java.nio.file.Files;
import java.nio.file.Path;

final class ModulePaths {

    private ModulePaths() {
    }

    static Path locateSpringBootStarterModuleRoot() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        for (Path current = cwd; current != null; current = current.getParent()) {
            if (Files.isDirectory(current.resolve("spring-boot-starter-common"))
                    && Files.isDirectory(current.resolve("spring-boot-starter-runner"))) {
                return current;
            }
        }
        throw new IllegalStateException(
                "未找到 spring-boot-starter-module 根目录（需含 spring-boot-starter-common 与 spring-boot-starter-runner）");
    }
}
