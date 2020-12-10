package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaResponse extends AbstractResponse {
    private String secret;
    private String image;
}
