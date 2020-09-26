package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessfullyAddedPost extends AbstractResponse {
    private boolean result;
}
