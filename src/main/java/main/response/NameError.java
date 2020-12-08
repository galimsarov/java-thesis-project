package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameError extends AbstractError {
    private String name;
}
