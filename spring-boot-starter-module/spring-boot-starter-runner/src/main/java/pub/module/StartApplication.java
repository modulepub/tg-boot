package pub.module;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import pub.module.common.plugin.bootstrap.ExternalPluginBootstrap;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * 单体启动类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@SpringBootApplication
@EnableCaching
public class StartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        SpringApplicationBuilder b = application.sources(StartApplication.class);
        for (Class<?> c : ExternalPluginBootstrap.resolveExtraPrimarySources(new String[0])) {
            b = b.sources(c);
        }
        return b;
    }

    public static void main(String[] args) throws UnknownHostException {
        List<Class<?>> primary = new ArrayList<>();
        primary.add(StartApplication.class);
        primary.addAll(ExternalPluginBootstrap.resolveExtraPrimarySources(args));
        SpringApplication app = new SpringApplication(primary.toArray(Class<?>[]::new));
        ConfigurableApplicationContext application = app.run(args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String printStr = getString(ip, port, path);
        System.out.println(printStr);

    }

    @NotNull
    private static String getString(String ip, String port, String path) {
        String printStr = """
                Application is running! Access URLs:
                Swagger: \t\t\t\thttp://localhost:{port}{path}/swagger-ui/index.html
                """;
        printStr = printStr.replace("{ip}", ip);
        printStr = printStr.replace("{port}", port != null ? port : "??");
        printStr = printStr.replace("{path}", path !=null? path :"");
        return printStr;
    }

}