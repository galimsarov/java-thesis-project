package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorAddingComment extends AbstractResponse{
    private boolean result;
    private TextError errors;
}
