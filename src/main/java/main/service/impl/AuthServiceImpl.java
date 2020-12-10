package main.service.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
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
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
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

    @Value("${captchaTime}")
    private int captchaTime;
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
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        if (authConfiguration.getAuths().containsKey(currentSession)) {
            User user = userRepository.findById
                    (authConfiguration.getAuths().get(currentSession)).get();
            return getAuthUserResponse(user);
        }
        else {
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
            String output = generateSecret();
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
        // TODO: ну надо сделать)))
        return null;
    }

    /**
     * Метод register
     * Метод создаёт пользователя в базе данных, если введённые данные верны
     *
     * @see main.request.UserRequest
     */
    @Override
    public AbstractResponse register(UserRequest request) {
        // TODO: ну надо сделать)))
        return null;
    }

    /**
     * Метод captcha
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в
     * базу данных и возвращает секретный код secret и изображение размером
     * 100х35
     */
    @Override
    public AbstractResponse captcha() throws IOException {
        CaptchaCode captchaCode = new CaptchaCode();
        Cage cage = new GCage();
        OutputStream outputStream = new FileOutputStream(
                "captcha.jpg", false);

        String code = cage.getTokenGenerator().next();
        String secretCode = generateSecret();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secretCode);
        captchaCode.setTime(new Date());
        captchaCodeRepository.saveAndFlush(captchaCode);

        cage.draw(code, outputStream);
        outputStream.close();

        File file = new File("captcha.jpg");

        BufferedImage image = ImageIO.read(file);
        BufferedImage newImage = new BufferedImage(
                100, 35, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 100; x++)
            for (int y = 0; y < 35; y++) {
                int rgb = image.getRGB(x * 2, y * 2);
                newImage.setRGB(x, y, rgb);
            }
        File newFile = new File("smallCaptcha.jpg");
        ImageIO.write(newImage, "jpg", newFile);
        String imageCode = Base64.getEncoder().encodeToString(
                FileUtils.readFileToByteArray(newFile));

        CaptchaResponse response = new CaptchaResponse();
        response.setSecret(secretCode);
        response.setImage("data:image/png;base64, " + imageCode);

        captchaCodeRepository.deleteOldCaptchas(captchaTime);

        file.delete();
        newFile.delete();

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

    private String generateSecret() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789"
                .toCharArray();
        StringBuilder sb = new StringBuilder(45);
        Random random = new Random();
        for (int i = 0; i < 45; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
