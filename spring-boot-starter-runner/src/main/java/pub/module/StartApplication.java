package pub.module;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 单体启动类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Slf4j
@SpringBootApplication
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
        String printStr = getString(ip, port, path);
        System.out.println(printStr);

    }

    @NotNull
    private static String getString(String ip, String port, String path) {
        String printStr = """
                Application is running! Access URLs:
                code generator: \t\thttp://localhost:{port}{path}/pub/generator/index
                Local: \t\t\t\t\t\thttp://localhost:{port}{path}
                Swagger: \t\t\t\thttp://localhost:{port}{path}/doc.html
                """;
        printStr = printStr.replace("{ip}", ip);
        printStr = printStr.replace("{port}", port != null ? port : "??");
        printStr = printStr.replace("{path}", path !=null? path :"");
        return printStr;
    }

}