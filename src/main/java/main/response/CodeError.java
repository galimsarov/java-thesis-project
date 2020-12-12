package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeError extends AbstractError {
    private String code;
}
