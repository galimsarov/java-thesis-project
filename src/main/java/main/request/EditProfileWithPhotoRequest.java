package main.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditProfileWithPhotoRequest {
    private MultipartFile photo;
    private String name;
    private String email;
    private String password;
}
