package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaError extends AbstractError {
    private String captcha;
}
