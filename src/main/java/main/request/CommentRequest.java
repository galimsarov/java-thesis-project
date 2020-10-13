package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private int parent_id;
    private int post_id;
    private String text;
}
