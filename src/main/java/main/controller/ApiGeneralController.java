package main.controller;

import main.request.*;
import main.response.AbstractResponse;
import main.response.Blog;
import main.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * Класс ApiGeneralController
 * REST-контроллер для прочих запросов к API
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    @Autowired
    private GeneralService generalService;

    /**
     * Метод getCommonData
     * Метод возвращает общую информацию о блоге
     * GET запрос /api/init
     *
     * @see Blog
     */
    @GetMapping("/init")
    public Blog getCommonData() {
        return generalService.getBlogInfo();
    }

    /**
     * Метод imageUpload
     * Метод загружает на сервер изображение в папку upload
     * POST запрос /api/image
     */
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public Object imageUpload(
            @RequestPart(value = "image") MultipartFile file)
            throws IOException {
        return generalService.imageUpload(file);
    }

    /**
     * Метод sendComment
     * Метод добавляет комментарий к посту
     * POST запрос /api/comment
     *
     * @see main.request.CommentRequest
     */
    @PostMapping("/comment")
    public Object sendComment(@RequestBody CommentRequest commentRequest) {
        AbstractResponse response = generalService
                .sendComment(commentRequest);
        return Objects.requireNonNullElseGet
                (response, () -> new ResponseEntity(HttpStatus.BAD_REQUEST));
    }

    /**
     * Метод getListOfTags
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в
     * параметре
     * GET запрос /api/tag
     *
     * @param query часть тэга или тэг, м.б. не задан, м.б. пустым
     * @see main.response.TagWithWeight
     */
    @GetMapping("/tag")
    public AbstractResponse getListOfTags(
            @RequestParam(required = false) String query) {
        return generalService.getListOfTags(query);
    }

    /**
     * Метод postModeration
     * Метод фиксирует действие модератора по посту: его утверждение или отклонение
     * POST запрос /api/moderation
     *
     * @see main.request.PostModerationRequest
     */
    @PostMapping("/moderation")
    public AbstractResponse postModeration(
            @RequestBody PostModerationRequest request) {
        return generalService.postModeration(request);
    }

    /**
     * Метод numberOfPosts
     * Метод выводит количества публикаций на каждую дату переданного в параметре
     * year года или текущего года
     * GET запрос /api/calendar
     *
     * @param year год в виде четырёхзначного числа, если не передан - возвращать
     *             за текущий год
     */
    @GetMapping("/calendar")
    public AbstractResponse numberOfPosts(
            @RequestParam(required = false) Integer year) {
        return generalService.numberOfPosts(year);
    }

    /**
     * Метод editProfile
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля
     * POST запрос /api/profile/my
     *
     * @see EditProfileWithPasswordRequest
     */
    @PostMapping(value = "/profile/my", consumes = "application/json")
    public AbstractResponse editProfile(
            @RequestBody EditProfileWithPasswordRequest request) {
        return generalService.editProfile(request);
    }

    /**
     * Метод editProfile
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля
     * POST запрос /api/profile/my
     *
     * @see EditProfileWithPhotoRequest
     */
    @PostMapping("/profile/my")
    public AbstractResponse editProfile(
            @ModelAttribute EditProfileWithPhotoRequest request)
            throws IOException {
        return generalService.editProfile(request);
    }
}
