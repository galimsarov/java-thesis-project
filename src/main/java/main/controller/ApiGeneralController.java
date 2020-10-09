package main.controller;

import main.response.Blog;
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
}
