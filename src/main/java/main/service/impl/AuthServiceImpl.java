package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.AuthRequest;
import main.response.AbstractResponse;
import main.response.SuccessfullyAddedPost;
import main.response.SuccessfullyLogin;
import main.response.UserAuthResponse;
import main.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

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
