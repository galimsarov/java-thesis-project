package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorAddingImage {
    private boolean result;
    private ImageError errors;
}
