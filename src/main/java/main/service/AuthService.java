package main.service;

import main.request.BasicRequest;
import main.response.BasicResponse;

import java.io.IOException;

public interface AuthService {
    BasicResponse login(BasicRequest authRequest);
    BasicResponse check();
    BasicResponse restore(BasicRequest emailRequest);
    Object changePassword(BasicRequest request);
    Object register(BasicRequest request);
    BasicResponse captcha() throws IOException;
    BasicResponse logout();
}
