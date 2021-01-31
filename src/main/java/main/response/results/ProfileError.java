package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.passwords.EmailNamePhotoResp;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileError extends ResultResponse {
    private EmailNamePhotoResp errors;
}
