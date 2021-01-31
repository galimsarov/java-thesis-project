package main.service.impl;

import com.github.cage.Cage;
import com.github.cage.GCage;
import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.model.CaptchaCode;
import main.model.User;
import main.repository.CaptchaCodeRepository;
import main.repository.GlobalSettingsRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.others.EmailRequest;
import main.request.passwords.ChangePasswordRequest;
import main.request.passwords.LoginRequest;
import main.request.passwords.RegisterRequest;
import main.response.ids.AuthUserResp;
import main.response.others.CaptchaResponse;
import main.response.passwords.CodePasCapResp;
import main.response.passwords.EmailNameCapResp;
import main.response.results.PasswordError;
import main.response.results.AuthResUserResp;
import main.response.results.RegisterError;
import main.response.results.ResultResponse;
import main.service.AuthService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.*;

/**
 * Класс AuthServiceImpl
 * Сервисный слой прочих запросов /api/auth/*
 *
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final AuthConfiguration authConfiguration;
    private final JavaMailSender javaMailSender;
    private final HttpServletRequest httpServletRequest;

    @Value("${captchaTime}")
    private int captchaTime;
    /**
     * Метод login
     * Метод проверяет введенные данные и производит авторизацию пользователя
     */
    @Override
    public ResultResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getE_mail());
        if (user.getPassword().equals(request.getPassword())) {
            AuthResUserResp response = getAuthUserResponse(user);
            String sessionId = RequestContextHolder
                    .currentRequestAttributes().getSessionId();
            authConfiguration.addAuth(sessionId, user.getId());
            return response;
        }
        else {
            ResultResponse response = new ResultResponse();
            response.setResult(false);
            return response;
        }
    }

    /**
     * Метод check
     * Метод возвращает информацию о текущем авторизованном пользователе
     */
    @Override
    public ResultResponse check() {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        if (authConfiguration.getAuths().containsKey(currentSession)) {
            User user = userRepository.findById
                    (authConfiguration.getAuths().get(currentSession)).get();
            return getAuthUserResponse(user);
        }
        else {
            ResultResponse response = new ResultResponse();
            response.setResult(false);
            return response;
        }
    }

    /**
     * Метод restore
     * Метод проверяет наличие в базе пользователя с указанным e-mail. Если
     * пользователь найден, ему должно отправляться письмо со ссылкой на
     * восстановление пароля
     */
    @Override
    public ResultResponse restore(EmailRequest request) {
        ResultResponse response = new ResultResponse();
        User user = userRepository.findByEmail(request.getEmail());
        if (user != null) {
            String output = generateSecret();
            user.setCode(output);
            userRepository.saveAndFlush(user);

            output = httpServletRequest.getScheme() + "://" +
                    httpServletRequest.getServerName() + ":" +
                    httpServletRequest.getServerPort()
                    + "/login/change-password/" + output;

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(request.getEmail());
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
     */
    @Override
    public ResultResponse changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByCode(request.getCode());
        CodePasCapResp errors = new CodePasCapResp();
        if (user != null) {
            String secretCode = captchaCodeRepository
                    .findSecretByCode(request.getCaptcha());
            if (secretCode == null)
                errors.setCaptcha("Код с картинки введён неверно");
            if (request.getPassword().length() < 6)
                errors.setPassword("Пароль короче 6-ти символов");
        }
        else
            errors.setCode("Ссылка для восстановления пароля устарела. " +
                    "<a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>");
        if ((errors.getCaptcha() != null) ||
                (errors.getPassword() != null) ||
                (errors.getCode() != null)) {
            PasswordError response = new PasswordError();
            response.setResult(false);
            response.setErrors(errors);
            return response;
        }
        else {
            user.setPassword(request.getPassword());
            userRepository.saveAndFlush(user);
            ResultResponse response = new ResultResponse();
            response.setResult(true);
            return response;
        }
    }

    /**
     * Метод register
     * Метод создаёт пользователя в базе данных, если введённые данные верны
     */
    @Override
    public Object register(RegisterRequest request) {
        String multiUser = globalSettingsRepository.multiUser();
        if (multiUser.equals("NO"))
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else {
            EmailNameCapResp errors = new EmailNameCapResp();
            if (userRepository.findByEmail(request.getE_mail()) != null)
                errors.setEmail("Этот e-mail уже зарегистрирован");
            if (request.getName().length() == 0)
                errors.setName("Имя указано неверно");
            if (request.getPassword().length() < 6)
                errors.setPassword("Пароль короче 6-ти символов");
            if (!request.getCaptcha_secret().equals(
                    captchaCodeRepository.findSecretByCode(request.getCaptcha())))
                errors.setCaptcha("Код с картинки введён неверно");

            if ((errors.getEmail() != null) ||
                    (errors.getName() != null) ||
                    (errors.getPassword() != null) ||
                    (errors.getCaptcha() != null)) {
                RegisterError response = new RegisterError();;
                response.setResult(false);
                response.setErrors(errors);
                return response;
            }
            else {
                User user = new User();
                user.setEmail(request.getE_mail());
                user.setPassword(request.getPassword());
                user.setName(request.getName());
                user.setRegTime(new Date());

                userRepository.saveAndFlush(user);

                ResultResponse response = new ResultResponse();
                response.setResult(true);

                return response;
            }
        }
    }

    /**
     * Метод captcha
     * Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в
     * базу данных и возвращает секретный код secret и изображение размером
     * 100х35
     */
    @Override
    public CaptchaResponse captcha() throws IOException {
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

    /**
     * Метод logout
     * Метод разлогинивает пользователя: удаляет идентификатор его сессии из
     * списка авторизованных
     */
    @Override
    public ResultResponse logout() {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        authConfiguration.deleteAuth(currentSession);
        ResultResponse response = new ResultResponse();
        response.setResult(true);
        return response;
    }

    private AuthResUserResp getAuthUserResponse(User userFromDB) {
        AuthUserResp user = new AuthUserResp();
        user.setId(userFromDB.getId());
        user.setName(userFromDB.getName());
        user.setPhoto(userFromDB.getPhoto());
        user.setEmail(userFromDB.getEmail());
        if (userFromDB.isModerator()) {
            int moderationCount = postRepository
                    .getCountOfPostsForModeration(user.getId());
            user.setModeration(true);
            user.setModerationCount(moderationCount);
            user.setSettings(true);
        }
        else {
            user.setModeration(false);
            user.setModerationCount(0);
            user.setSettings(false);
        }
        AuthResUserResp response = new AuthResUserResp();
        response.setResult(true);
        response.setUser(user);
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
