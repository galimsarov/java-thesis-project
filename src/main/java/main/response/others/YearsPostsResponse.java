package main.response.others;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class YearsPostsResponse extends PostsResponse {
    private List<Integer> years;
}
