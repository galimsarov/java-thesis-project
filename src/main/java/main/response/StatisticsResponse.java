package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsResponse extends AbstractResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
