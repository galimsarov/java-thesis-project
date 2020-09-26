package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListOfPostsResponse {
    private int count;
    private List<PostResponse> posts;
}
