package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
      private String code;
      private String password;
      private String captcha;
      private String captcha_secret;
}
