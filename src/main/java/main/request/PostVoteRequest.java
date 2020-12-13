package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostVoteRequest {
    @JsonProperty("post_id")
    private int postId;
}
