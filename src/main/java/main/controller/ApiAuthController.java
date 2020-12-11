package main.controller;

import main.request.AuthRequest;
import main.request.ChangePasswordRequest;
import main.request.EmailRequest;
import main.request.UserRequest;
import main.response.AbstractResponse;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

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

    /**
     * Метод changePassword
     * Метод проверяет корректность кода восстановления пароля (параметр code)
     * и корректность кодов капчи
     * POST запрос /api/auth/password
     *
     * @see main.request.ChangePasswordRequest
     */
    @PostMapping("/password")
    public AbstractResponse changePassword(
            @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }

    /**
     * Метод register
     * Метод создаёт пользователя в базе данных, если введённые данные верны
     * POST запрос /api/auth/register
     *
     * @see main.request.UserRequest
     */
    @PostMapping("/register")
    public Object register(@RequestBody UserRequest request) {
        return authService.register(request);
    }

    /**
     * Метод captcha
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в
     * базу данных и возвращает секретный код secret и изображение размером
     * 100х35
     * GET запрос /api/auth/captcha
     */
    @GetMapping("/captcha")
    public AbstractResponse captcha() throws IOException {
        return authService.captcha();
    }
}
