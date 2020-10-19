package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostModerationRequest {
    private int post_id;
    private String decision;
}
