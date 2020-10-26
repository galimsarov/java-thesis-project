package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfullyLogin extends AbstractResponse {
    private boolean result;
    private UserAuthResponse user;
}
