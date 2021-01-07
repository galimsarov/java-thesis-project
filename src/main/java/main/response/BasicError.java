package main.response;

import lombok.Data;

/**
 * Класс BasicError
 * Базовый класс для ошибок. Если какие-то поля не нужны, они не
 * инициализируются
 *
 * @version 1.0
 */
@Data
public class BasicError {
    private boolean result;

    private BasicError errors;

    private String email;
    private String name;
    private String password;
    private String captcha;
    private String code;
    private String photo;
}
