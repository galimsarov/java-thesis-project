package main.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Класс BasicRequest
 * Базовый класс для запросов. Если какие-то поля не нужны, они не
 * инициализируются
 *
 * @version 1.0
 */
@Data
public class BasicRequest {
    private long timestamp;

    private int active;
    private int parent_id;
    private int post_id;
    private int removePhoto;

    private String e_mail;
    private String email;
    private String code;
    private String decision;
    private String password;
    private String name;
    private String captcha;
    private String captcha_secret;
    private String title;
    private String text;

    private List<String> tags;

    private MultipartFile photo;
}
