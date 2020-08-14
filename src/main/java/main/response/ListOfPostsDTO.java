package main.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListOfPostsDTO {
    private int count;
    private List<PostDTO> posts;
}
