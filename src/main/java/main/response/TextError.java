package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextError extends AbstractError {
    private String text;
}
