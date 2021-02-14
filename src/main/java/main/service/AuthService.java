package main.service;

import main.model.request.others.EmailRequest;
import main.model.request.passwords.ChangePasswordRequest;
import main.model.request.passwords.LoginRequest;
import main.model.request.passwords.RegisterRequest;
import main.model.response.others.CaptchaResponse;
import main.model.response.results.ResultResponse;

import java.io.IOException;

public interface AuthService {
    ResultResponse login(LoginRequest authRequest);
    ResultResponse check();
    ResultResponse restore(EmailRequest emailRequest);
    ResultResponse changePassword(ChangePasswordRequest request);
    Object register(RegisterRequest request);
    CaptchaResponse captcha() throws IOException;
    ResultResponse logout();
}
