package main.config;

import main.response.Blog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс InitConfiguration
 * Конфигуратор общей информации по блогу
 *
 * @version 1.0
 */
@Configuration
public class InitConfiguration {
    @Value("${title}")
    private String title;

    @Value("${subtitle}")
    private String subtitle;

    @Value("${phone}")
    private String phone;

    @Value("${email}")
    private String email;

    @Value("${copyright}")
    private String copyright;

    @Value("${copyrightFrom}")
    private String copyrightFrom;

    /**
     * Метод getBlogInfo
     * Метод возвращает общую информацию о блоге
     *
     * @see Blog
     */
    @Bean
    public Blog getBlogInfo() {
        return new Blog(title, subtitle, phone, email, copyright, copyrightFrom);
    }
}
