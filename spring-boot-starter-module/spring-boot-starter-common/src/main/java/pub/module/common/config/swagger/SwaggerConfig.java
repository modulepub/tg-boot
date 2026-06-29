package pub.module.common.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 * @author PZ
 * @since 2026-01-02
 * @version V1.0
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        Contact contact = new Contact();
        contact.setName("pz@module.pub");

        return new OpenAPI().info(new Info()
                .title("TG API")
                .description("TG一个基于Spring Boot，完全尊重spring boot的设计理念的框架")
                .contact(contact)
                .version("1.0.0")
                .termsOfService("https://module.pub")
        );
    }

}