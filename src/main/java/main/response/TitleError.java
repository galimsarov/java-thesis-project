package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TitleError extends AbstractError {
    private String title;
}
