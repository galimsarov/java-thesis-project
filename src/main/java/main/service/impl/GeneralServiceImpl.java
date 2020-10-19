package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.CommentRequest;
import main.response.*;
import main.service.GeneralService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Класс GeneralServiceImpl
 * Сервисный слой прочих запросов к API
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService {
    private final Blog blog;
    private final HttpServletRequest request;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository commentRepository;

    @PersistenceContext
    private EntityManager entityManager;

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

    /**
     * Метод getListOfTags
     * Метод выдаёт список тэгов, начинающихся на строку, заданную в параметре
     *
     * @param query часть тэга или тэг, м.б. не задан, м.б. пустым
     * @see main.response.TagWithWeight
     */
    @Override
    public AbstractResponse getListOfTags(String query) {
        Query nativeQuery = entityManager.createNativeQuery
                ("select name, count(name) from " +
                        "(select tags.name, new_tag2posts.postid from tags " +
                        "join (select tag2post.tag_id, new_posts.id " +
                        "as postid from tag2post join (select * from posts " +
                        "where is_active = 1 and moderation_status = " +
                        "'ACCEPTED' and time < current_time()) as " +
                        "new_posts where tag2post.post_id = new_posts.id) " +
                        "as new_tag2posts on tags.id = new_tag2posts.tag_id) " +
                        "as ready_tags where name like concat(?1,'%') " +
                        "group by name");
        nativeQuery.setParameter(1, query);
        List<Object[]> tags = nativeQuery.getResultList();
        int totalPosts = postRepository.getActivePosts();

        List<TagWithWeight> list = new ArrayList<>();
        ListOfTags response = new ListOfTags();

        BigInteger currentBig = new BigInteger(tags.get(0)[1].toString());
        int currentCount = currentBig.intValue();
        float maxWeight = (float)(currentCount) / totalPosts;

        for (Object[] objects : tags) {
            TagWithWeight tag = new TagWithWeight();
            currentBig = new BigInteger(objects[1].toString());
            currentCount = currentBig.intValue();
            float currentWeight = (float) (currentCount) / totalPosts;
            if (currentWeight > maxWeight)
                maxWeight = currentWeight;
            tag.setName(objects[0].toString());
            tag.setWeight(currentWeight);
            list.add(tag);
        }
        for (TagWithWeight tagWithWeight : list) {
            tagWithWeight.setWeight
                    (tagWithWeight.getWeight()/maxWeight);
        }
        response.setTags(list);
        return response;
    }
}
