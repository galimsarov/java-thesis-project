package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.passwords.EmailNameCapResp;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterError extends ResultResponse {
    private EmailNameCapResp errors;
}
