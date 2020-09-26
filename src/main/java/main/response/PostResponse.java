package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {
    private int id;
    private long timestamp;
    private UserBasicResponse user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
}
