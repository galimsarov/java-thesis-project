package main.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import main.model.response.others.Blog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс InitConfiguration
 * Конфигуратор общей информации по блогу, а также настройки cloudinary
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

    @Value("${cloudName}")
    private String cloudName;

    @Value("${apiKey}")
    private String apiKey;

    @Value("${apiSecret}")
    private String apiSecret;

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

    /**
     * Метод getCloudinary
     * Метод объект для работы с cloudinary
     */
    @Bean
    public Cloudinary getCloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
