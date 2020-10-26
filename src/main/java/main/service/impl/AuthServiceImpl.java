package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.User;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.AuthRequest;
import main.response.AbstractResponse;
import main.response.SuccessfullyAddedPost;
import main.response.SuccessfullyLogin;
import main.response.UserAuthResponse;
import main.service.AuthService;
import org.springframework.stereotype.Service;

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
        else {
            SuccessfullyAddedPost response = new SuccessfullyAddedPost();
            response.setResult(false);
            return response;
        }
    }
}
