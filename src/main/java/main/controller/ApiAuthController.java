package main.controller;

import main.request.BasicRequest;
import main.response.BasicResponse;
import main.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Класс ApiAuthController
 * REST-контроллер, обрабатывает все запросы /api/auth/*
 *
 * @version 1.1
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
     */
    @PostMapping("/login")
    public BasicResponse login(@RequestBody BasicRequest request) {
        return authService.login(request);
    }

    /**
     * Метод check
     * Метод возвращает информацию о текущем авторизованном пользователе
     * GET запрос /api/auth/check
     */
    @GetMapping("/check")
    public BasicResponse check() {
        return authService.check();
    }

    /**
     * Метод restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если
     * пользователь найден, ему должно отправляться письмо со ссылкой на
     * восстановление пароля
     * POST запрос /api/auth/restore
     */
    @PostMapping("/restore")
    public BasicResponse restore(@RequestBody BasicRequest request) {
        return authService.restore(request);
    }

    /**
     * Метод changePassword
     * Метод проверяет корректность кода восстановления пароля (параметр code)
     * и корректность кодов капчи
     * POST запрос /api/auth/password
     */
    @PostMapping("/password")
    public Object changePassword(
            @RequestBody BasicRequest request) {
        return authService.changePassword(request);
    }

    /**
     * Метод register
     * Метод создаёт пользователя в базе данных, если введённые данные верны
     * POST запрос /api/auth/register
     */
    @PostMapping("/register")
    public Object register(@RequestBody BasicRequest request) {
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
    public BasicResponse captcha() throws IOException {
        return authService.captcha();
    }

    /**
     * Метод logout
     * Метод разлогинивает пользователя: удаляет идентификатор его сессии из
     * списка авторизованных
     * GET запрос /api/auth/logout
     */
    @GetMapping("/logout")
    public BasicResponse logout() {
        return authService.logout();
    }
}
