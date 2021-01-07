package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SpecificPostResponse {
    private int id;
    private long timestamp;
    private boolean active;
    private UserBasicResponse user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<AdditionalResponse> comments;
    private Set<String> tags;
}
