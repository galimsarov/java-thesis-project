package main.request.passwords;

import lombok.Data;

@Data
public abstract class PasswordRequest {
    private String password;
}
