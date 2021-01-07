package main.response;

import lombok.Data;

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
