package main.service.impl;

import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.CommentRequest;
import main.response.*;
import main.service.GeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostCommentRepository commentRepository;

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

    /**
     * Метод sendComment
     * Метод добавляет комментарий к посту
     *
     * @see main.request.CommentRequest
     */
    @Override
    public AbstractResponse sendComment(CommentRequest commentRequest) {
        if (commentRequest.getText().length() < 3) {
            TextError textError = new TextError();
            if (commentRequest.getText().length() == 0)
                textError.setText("Комментарий не установлен");
            else
                textError.setText("Текст комментария слишком короткий");
            ErrorAddingComment errorResponse = new ErrorAddingComment();
            errorResponse.setResult(false);
            errorResponse.setErrors(textError);
            return errorResponse;
        }
        IdResponse response = null;
        try {
            Post post = postRepository.getOne(commentRequest.getPost_id());
            PostComment comment = new PostComment();

            PostComment parentComment = commentRepository
                    .getOne(commentRequest.getParent_id());
            if (parentComment.getId() != 0) {
                String tempText = parentComment.getText();
            }

            Authentication auth = SecurityContextHolder.getContext().
                    getAuthentication();
            User user = userRepository.findByName(auth.getName());

            comment.setParentId(commentRequest.getParent_id());
            comment.setPost(post);
            comment.setText(commentRequest.getText());
            comment.setTime(new Date());
            comment.setUser(user);

            post.addPostComment(comment);

            postRepository.saveAndFlush(post);

            response = new IdResponse();
            response.setId(commentRepository.findIdByText(comment.getText()));
        }
        catch (EntityNotFoundException e) {
            return response;
        }
        return response;
    }
}
