package main.controller;

import main.request.*;
import main.response.*;
import main.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     */
    @PostMapping("/comment")
    public BasicResponse sendComment(@RequestBody BasicRequest request) {
        return generalService.sendComment(request);
    }

    /**
     * Метод getListOfTags
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в
     * параметре
     * GET запрос /api/tag
     *
     * @param query часть тэга или тэг, м.б. не задан, м.б. пустым
     */
    @GetMapping("/tag")
    public AdditionalResponse getListOfTags(
            @RequestParam(required = false) String query) {
        return generalService.getListOfTags(query);
    }

    /**
     * Метод postModeration
     * Метод фиксирует действие модератора по посту: его утверждение или
     * отклонение
     * POST запрос /api/moderation
     */
    @PostMapping("/moderation")
    public BasicResponse postModeration(
            @RequestBody BasicRequest request) {
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
    public AdditionalResponse numberOfPosts(
            @RequestParam(required = false) Integer year) {
        return generalService.numberOfPosts(year);
    }

    /**
     * Метод editProfile
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля, без изменения фото
     * POST запрос /api/profile/my
     */
    @PostMapping(value = "/profile/my", consumes = "application/json")
    public Object editProfile(
            @RequestBody BasicRequest request) {
        return generalService.editProfile(request);
    }

    /**
     * Метод editProfileWithPhoto
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля, с изменением фото
     * POST запрос /api/profile/my
     */
    @PostMapping("/profile/my")
    public Object editProfileWithPhoto(
            @ModelAttribute BasicRequest request)
            throws IOException {
        return generalService.editProfileWithPhoto(request);
    }

    /**
     * Метод myStatistics
     * Метод возвращает статистику постов текущего авторизованного пользователя
     * GET запрос /api/statistics/my
     */
    @GetMapping("/statistics/my")
    public BasicResponse myStatistics() {
        return generalService.myStatistics();
    }

    /**
     * Метод allStatistics
     * Метод возвращает статистику  всем постам блога
     * GET запрос /api/statistics/all
     */
    @GetMapping("/statistics/all")
    public Object allStatistics() {
        return generalService.allStatistics();
    }

    /**
     * Метод getSettings
     * Метод возвращает глобальные настройки блога из таблицы global_settings
     * GET запрос /api/settings
     */
    @GetMapping("/settings")
    public BasicResponse getSettings() {
        return generalService.getSettings();
    }

    /**
     * Метод putSettings
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором
     * PUT запрос /api/settings
     */
    @PutMapping("/settings")
    public void putSettings(@RequestBody AdditionalRequest request) {
        generalService.putSettings(request);
    }
}
