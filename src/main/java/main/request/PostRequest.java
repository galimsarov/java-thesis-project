package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {
    private long timestamp;
    private int active;
    private String title;
    private String[] tags;
    private String text;
}
