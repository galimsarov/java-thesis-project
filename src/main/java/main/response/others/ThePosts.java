package main.response.others;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThePosts extends PostsResponse {
    private int count;
}
