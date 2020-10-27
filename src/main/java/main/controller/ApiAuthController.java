package main.controller;

import main.request.AuthRequest;
import main.request.EmailRequest;
import main.response.AbstractResponse;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Метод check
     * Метод возвращает информацию о текущем авторизованном пользователе
     * GET запрос /api/auth/check
     *
     * @see main.response.SuccessfullyLogin
     */
    @GetMapping("/check")
    public AbstractResponse check() {
        return authService.check();
    }

    /**
     * Метод restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если
     * пользователь найден, ему должно отправляться письмо со ссылкой на
     * восстановление пароля
     * POST запрос /api/auth/restore
     *
     * @see main.request.EmailRequest
     */
    @PostMapping("/restore")
    public AbstractResponse restore(@RequestBody EmailRequest emailRequest) {
        return authService.restore(emailRequest);
    }
}
