package main.response.others;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CaptchaResponse extends ImageResponse {
    private String secret;
}
