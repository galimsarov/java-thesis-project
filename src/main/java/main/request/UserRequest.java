package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String e_mail;
    private String password;
    private String name;
    private String captcha;
    private String captcha_secret;
}
