package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.others.TextResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentError extends ResultResponse {
    private TextResponse errors;
}
