package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.passwords.CodePasCapResp;

@EqualsAndHashCode(callSuper = true)
@Data
public class PasswordError extends ResultResponse {
    private CodePasCapResp errors;
}
