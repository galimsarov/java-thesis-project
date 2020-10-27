package main.service;

import main.request.AuthRequest;
import main.request.EmailRequest;
import main.response.AbstractResponse;

public interface AuthService {
    AbstractResponse login(AuthRequest authRequest);
    AbstractResponse check();
    AbstractResponse restore(EmailRequest emailRequest);
}
