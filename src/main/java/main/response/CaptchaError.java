package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaError {
    private final String code = "Ссылка для восстановления пароля устарела. " +
            "<a href=\"/auth/restore\">Запросить ссылку снова</a>";
    private final String password = "Пароль короче 6-ти символов";
    private final String captcha = "Код с картинки введён неверно";
}
