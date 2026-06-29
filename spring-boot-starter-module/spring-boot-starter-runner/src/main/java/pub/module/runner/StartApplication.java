package pub.module.runner;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 单体启动类（置于 runner 子包，避免 MyBatis 将 {@code pub.module} 根包当作 AutoConfiguration 扫描路径）。
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "pub.module")
@EnableCaching
public class StartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StartApplication.class);
    }

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(StartApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        System.out.println(getString(ip, port, path));
    }

    @NotNull
    private static String getString(String ip, String port, String path) {
        String printStr = """
                Application is running! Access URLs:
                Swagger: \t\t\t\thttp://localhost:{port}{path}/swagger-ui/index.html
                """;
        printStr = printStr.replace("{ip}", ip);
        printStr = printStr.replace("{port}", port != null ? port : "??");
        printStr = printStr.replace("{path}", path != null ? path : "");
        return printStr;
    }
}
