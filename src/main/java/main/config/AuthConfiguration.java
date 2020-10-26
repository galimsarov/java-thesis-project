package main.config;

import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Класс AuthConfiguration
 * Конфигуратор информации по авторизациям
 *
 * @version 1.0
 */
@Configuration
public class AuthConfiguration {
    private final Map<String, Integer> authorizations;

    public AuthConfiguration(Map<String, Integer> authorizations) {
        this.authorizations = authorizations;
    }

    public void addAuth(String key, int value) {
        authorizations.put(key, value);
    }

//    @Bean
    public Map<String, Integer> getAuths() {
        return authorizations;
    }
}
