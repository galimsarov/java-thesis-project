package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailError extends AbstractError {
    private String email;
}
