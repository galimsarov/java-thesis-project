package main.service;

import main.request.AuthRequest;
import main.response.AbstractResponse;

public interface AuthService {
    AbstractResponse login(AuthRequest authRequest);
    AbstractResponse check();
}
