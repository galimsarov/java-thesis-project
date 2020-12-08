package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoError extends AbstractError {
    private String photo;
}
