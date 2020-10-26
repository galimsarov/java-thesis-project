package main.controller;

import main.request.AuthRequest;
import main.response.AbstractResponse;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Класс ApiAuthController
 * REST-контроллер, обрабатывает все запросы /api/auth/*
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {
    @Autowired
    private AuthService authService;

    /**
     * Метод login
     * Метод проверяет введенные данные и производит авторизацию пользователя
     * POST запрос /api/auth/login
     *
     * @see main.request.AuthRequest
     */
    @PostMapping("/login")
    public AbstractResponse login(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest);
    }
}
