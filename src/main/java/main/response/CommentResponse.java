package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserWithPhotoResponse user;
    private int parentId;
}
