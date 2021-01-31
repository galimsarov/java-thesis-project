package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.others.TitleTextResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostError extends ResultResponse {
    private TitleTextResponse errors;
}
