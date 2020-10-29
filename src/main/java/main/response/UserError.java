package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserError {
    private final String email = "Этот e-mail уже зарегистрирован";
    private final String name = "Имя указано неверно";
    private final String password = "Пароль короче 6-ти символов";
    private final String captcha = "Код с картинки введён неверно";
}
