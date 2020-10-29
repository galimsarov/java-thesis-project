package main.service;

import main.request.AuthRequest;
import main.request.ChangePasswordRequest;
import main.request.EmailRequest;
import main.request.UserRequest;
import main.response.AbstractResponse;

public interface AuthService {
    AbstractResponse login(AuthRequest authRequest);
    AbstractResponse check();
    AbstractResponse restore(EmailRequest emailRequest);
    AbstractResponse changePassword(ChangePasswordRequest request);
    AbstractResponse register(UserRequest request);
}
