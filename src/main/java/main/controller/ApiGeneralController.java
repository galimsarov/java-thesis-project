package main.controller;

import main.request.CommentRequest;
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
    GeneralService generalService;

    /**
     * Метод getCommonData
     * Метод возвращает общую информацию о блоге
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
    public Object imageUpload
            (@RequestPart(value = "image") MultipartFile file)
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
        AbstractResponse response = generalService.sendComment(commentRequest);
        return Objects.requireNonNullElseGet
                (response, () -> new ResponseEntity(HttpStatus.BAD_REQUEST));
    }
}
