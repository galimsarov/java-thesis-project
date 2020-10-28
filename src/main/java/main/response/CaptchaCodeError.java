package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaCodeError extends AbstractResponse {
    private final boolean result = false;
    private final CaptchaError errors = new CaptchaError();
}
