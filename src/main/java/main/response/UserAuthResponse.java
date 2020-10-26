package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuthResponse {
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
