package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.AuthRequest;
import main.request.ChangePasswordRequest;
import main.request.EmailRequest;
import main.request.UserRequest;
import main.response.*;
import main.service.AuthService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Random;

/**
 * Класс AuthServiceImpl
 * Сервисный слой прочих запросов /api/auth/*
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final AuthConfiguration authConfiguration;
    private final JavaMailSender javaMailSender;
    private final HttpServletRequest request;
    /**
     * Метод login
     * Метод проверяет введенные данные и производит авторизацию пользователя
     *
     * @see main.request.AuthRequest
     */
    @Override
    public AbstractResponse login(AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getE_mail());
        if (user.getPassword().equals(authRequest.getPassword())) {
            SuccessfullyLogin response = getAuthUserResponse(user);

            String sessionId = RequestContextHolder
                    .currentRequestAttributes().getSessionId();
            authConfiguration.addAuth(sessionId, user.getId());

            return response;
        }
        else {
            SuccessfullyAddedPost response = new SuccessfullyAddedPost();
            response.setResult(false);
            return response;
        }
    }

    /**
     * Метод check
     * Метод возвращает информацию о текущем авторизованном пользователе
     *
     * @see main.response.SuccessfullyLogin
     */
    @Override
    public AbstractResponse check() {

//        System.out.println("CHECK SERVICE STARTED\n");

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        if (authConfiguration.getAuths().containsKey(currentSession)) {

//            System.out.println("CHECK SERVICE AND CONTROLLER FINISHED: " +
//                        "successful!\n");

            User user = userRepository.findById
                    (authConfiguration.getAuths().get(currentSession)).get();
            return getAuthUserResponse(user);
        }
        else {
            SuccessfullyAddedPost response = new SuccessfullyAddedPost();
                response.setResult(false);

//            System.out.println("CHECK SERVICE AND CONTROLLER FINISHED: " +
//                        "error!\n");

            return response;
        }
    }

    /**
     * Метод restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если
     * пользователь найден, ему должно отправляться письмо со ссылкой на
     * восстановление пароля
     *
     * @see main.request.EmailRequest
     */
    @Override
    public AbstractResponse restore(EmailRequest emailRequest) {
        SuccessfullyAddedPost response = new SuccessfullyAddedPost();
        User user = userRepository.findByEmail(emailRequest.getEmail());
        if (user != null) {
            char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789"
                    .toCharArray();
            StringBuilder sb = new StringBuilder(45);
            Random random = new Random();
            for (int i = 0; i < 45; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            String output = sb.toString();

            user.setCode(output);
            userRepository.saveAndFlush(user);

            output = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort()
                    + "/login/change-password/" + output;

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(emailRequest.getEmail());
            msg.setSubject("Ссылка для восстановления пароля");
            msg.setText(output);
            javaMailSender.send(msg);

            response.setResult(true);
        }
        else
            response.setResult(false);
        return response;
    }

    /**
     * Метод changePassword
     * Метод проверяет корректность кода восстановления пароля (параметр code)
     * и корректность кодов капчи
     *
     * @see main.request.ChangePasswordRequest
     */
    @Override
    public AbstractResponse changePassword(ChangePasswordRequest request) {
        CaptchaCode captchaCode = captchaCodeRepository
                .findByCode(request.getCaptcha());
        if (captchaCode != null) {
            if (captchaCode.getSecretCode()
                    .equals(request.getCaptcha_secret())) {
                User user = userRepository.findByCode(request.getCode());
                if (user != null) {
                    user.setPassword(request.getPassword());
                    userRepository.saveAndFlush(user);
                }
                else
                    return new CaptchaCodeError();

                SuccessfullyAddedPost response = new SuccessfullyAddedPost();
                response.setResult(true);
                return response;
            }
            else
                return new CaptchaCodeError();
        }
        else
            return new CaptchaCodeError();
    }

    /**
     * Метод register
     * Метод создаёт пользователя в базе данных, если введённые данные верны
     *
     * @see main.request.UserRequest
     */
    @Override
    public AbstractResponse register(UserRequest request) {
        CaptchaCode captchaCode = captchaCodeRepository
                .findByCode(request.getCaptcha());
        if (captchaCode != null) {
            if (captchaCode.getSecretCode()
                    .equals(request.getCaptcha_secret())) {
                User user = userRepository.findByEmail(request.getE_mail());
                if (user == null) {
                    if (request.getPassword().length() >= 6) {
                        User newUser = new User();
                        newUser.setPassword(request.getPassword());
                        newUser.setEmail(request.getE_mail());
                        newUser.setName(request.getName());
                        newUser.setRegTime(new Date());
                        userRepository.saveAndFlush(newUser);

                        SuccessfullyAddedPost response =
                                new SuccessfullyAddedPost();
                        response.setResult(true);
                        return response;
                    }
                    else {
                        System.out.println("Пароль короче 6-ти символов");
                        return new UserCodeError();
                    }
                }
                else {
                    System.out.println("Этот e-mail уже зарегистрирован");
                    return new UserCodeError();
                }
            }
            else {
                System.out.println("Секретная капча не совпала");
                return new UserCodeError();
            }
        }
        else {
            System.out.println("Код капчи не найден");
            return new UserCodeError();
        }
    }

    private SuccessfullyLogin getAuthUserResponse(User user) {
        UserAuthResponse userResponse = new UserAuthResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());
        userResponse.setEmail(user.getEmail());
        if (user.isModerator()) {
            int moderationCount = postRepository
                    .getCountOfPostsForModeration(user.getId());
            userResponse.setModeration(true);
            userResponse.setModerationCount(moderationCount);
            userResponse.setSettings(true);
        }
        else {
            userResponse.setModeration(false);
            userResponse.setModerationCount(0);
            userResponse.setSettings(false);
        }

        SuccessfullyLogin response = new SuccessfullyLogin();
        response.setResult(true);
        response.setUser(userResponse);
        return response;
    }
}
