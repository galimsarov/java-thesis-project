package main.service;

import main.request.others.EmailRequest;
import main.request.passwords.ChangePasswordRequest;
import main.request.passwords.LoginRequest;
import main.request.passwords.RegisterRequest;
import main.response.others.CaptchaResponse;
import main.response.results.ResultResponse;

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
