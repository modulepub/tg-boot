package pub.module.common.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableConfigurationProperties(TgActuatorSecurityProperties.class)
@ConditionalOnProperty(prefix = "tg.actuator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TgActuatorSecurityConfig {

    public static final String ACTUATOR_PATH_PATTERN = "/actuator/**";
    public static final String ACTUATOR_ROLE = "ACTUATOR";

    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http,
                                                           TgActuatorSecurityProperties properties) throws Exception {
        http.securityMatcher(ACTUATOR_PATH_PATTERN)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole(ACTUATOR_ROLE))
                .httpBasic(Customizer.withDefaults())
                .userDetailsService(createActuatorUserDetailsService(properties));
        return http.build();
    }

    private static UserDetailsService createActuatorUserDetailsService(TgActuatorSecurityProperties properties) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        UserDetails user = User.builder()
                .username(properties.getUsername())
                .password("{bcrypt}" + encoder.encode(properties.getPassword()))
                .roles(ACTUATOR_ROLE)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    public static AntPathRequestMatcher actuatorRequestMatcher() {
        return new AntPathRequestMatcher(ACTUATOR_PATH_PATTERN);
    }
}
