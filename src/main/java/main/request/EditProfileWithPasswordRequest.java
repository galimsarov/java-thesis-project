package main.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileWithPasswordRequest {
    private String name;
    private String email;
    private String password;
    private int removePhoto;
}
