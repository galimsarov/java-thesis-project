package main.response.results;

import lombok.Data;
import lombok.EqualsAndHashCode;
import main.response.ids.AuthUserResp;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthResUserResp extends ResultResponse {
    private AuthUserResp user;
}
