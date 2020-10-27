package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.AuthRequest;
import main.request.EmailRequest;
import main.response.AbstractResponse;
import main.response.SuccessfullyAddedPost;
import main.response.SuccessfullyLogin;
import main.response.UserAuthResponse;
import main.service.AuthService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
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
            authConfiguration.addAuth(user.getName(), user.getId());
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
        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        User user = userRepository.findByName(auth.getName());
        Map<String, Integer> authorizations = authConfiguration.getAuths();
        try {
            if (authorizations.keySet().contains(user.getName()))
                return getAuthUserResponse(user);
            else {
                SuccessfullyAddedPost response = new SuccessfullyAddedPost();
                response.setResult(false);
                return response;
            }
        }
        catch (NullPointerException e) {
            SuccessfullyAddedPost response = new SuccessfullyAddedPost();
            response.setResult(false);
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
