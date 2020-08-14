package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private int id;
    private long timestamp;
    private UserDTOBasic user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
}
