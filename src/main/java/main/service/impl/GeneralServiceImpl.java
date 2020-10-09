package main.service.impl;

import main.response.Blog;
import main.response.ErrorAddingImage;
import main.response.ImageError;
import main.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Класс GeneralServiceImpl
 * Сервисный слой прочих запросов к API
 *
 * @version 1.0
 */
@Service
public class GeneralServiceImpl implements GeneralService {
    @Autowired
    private Blog blog;

    @Autowired
    private HttpServletRequest request;

    /**
     * Метод getBlogInfo
     * Метод возвращает общую информацию о блоге
     *
     * @see Blog
     */
    @Override
    public Blog getBlogInfo() {
        return blog;
    }

    /**
     * Метод imageUpload
     * Метод загружает на сервер изображение в папку upload
     */
    @Override
    public Object imageUpload(MultipartFile file) throws IOException {
        String response = null;

        if (!file.getOriginalFilename().endsWith("jpg") &&
                !file.getOriginalFilename().endsWith("png")) {
            ErrorAddingImage errorResponse = new ErrorAddingImage();
            errorResponse.setResult(false);
            ImageError imageError = new ImageError();
            imageError.setImage("Файл не является изображением");
            errorResponse.setErrors(imageError);
            return errorResponse;
        }

        if (file.getSize() > 1048576) {
            ErrorAddingImage errorResponse = new ErrorAddingImage();
            errorResponse.setResult(false);
            ImageError imageError = new ImageError();
            imageError.setImage("Размер файла превышает допустимый размер");
            errorResponse.setErrors(imageError);
            return errorResponse;
        }

        if (!file.isEmpty()) {
            String uploadsDir = "/upload/";
            String realPathToUploads = request.getServletContext()
                    .getRealPath(uploadsDir);

            String workPiece = "abcdefghijklmnopqrstuvwxyz";
            String[] subDirs = new String[3];
            for (int i = 0; i < 3; i++) {
                Random random = new Random();
                int range = random.nextInt(25);
                subDirs[i] = workPiece.substring(range, range + 2);
            }

            if (!new File(realPathToUploads).exists())
                new File(realPathToUploads).mkdir();

            for (String subDir : subDirs) {
                realPathToUploads += subDir + "/";
                if (!new File(realPathToUploads).exists())
                    new File(realPathToUploads).mkdir();
                uploadsDir += subDir + "/";
            }

            String orgName = file.getOriginalFilename();
            String filePath = realPathToUploads + orgName;
            File dest = new File(filePath);
            file.transferTo(dest);

            response = uploadsDir + file.getOriginalFilename();
        }
        return response;
    }
}
