package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordError extends AbstractError {
    private String password;
}
