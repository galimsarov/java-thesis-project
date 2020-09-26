package main.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWithPhotoResponse {
    private int id;
    private String name;
    private String photo;
}
