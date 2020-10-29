package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCodeError extends AbstractResponse {
    private final boolean result = false;
    private final UserError errors = new UserError();
}
