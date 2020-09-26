package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorAddingPost extends AbstractResponse {
    private boolean result;
    private List<AbstractError> errors;
}
