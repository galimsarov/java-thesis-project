package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.model.GlobalSetting;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.helper.PostStatus;
import main.repository.GlobalSettingsRepository;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.UserRepository;
import main.request.*;
import main.response.*;
import main.service.GeneralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

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
    private final GlobalSettingsRepository globalSettingsRepository;
    private final AuthConfiguration authConfiguration;

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
        BasicResponse errorResponse = new BasicResponse();
        errorResponse.setResult(false);
        BasicResponse errors = new BasicResponse();
        if (!file.getOriginalFilename().endsWith("jpg") &&
                !file.getOriginalFilename().endsWith("png"))
            errors.setImage("Файл не является изображением");
        if (file.getSize() > 1048576)
            errors.setImage("Размер файла превышает допустимый размер");
        if (errors.getImage() != null) {
            errorResponse.setErrors(errors);
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
     */
    @Override
    public BasicResponse sendComment(BasicRequest request) {
        BasicResponse response = new BasicResponse();
        if (request.getText().length() < 3) {
            BasicResponse errors = new BasicResponse();
            if (request.getText().length() == 0)
                errors.setText("Комментарий не установлен");
            else
                errors.setText("Текст комментария слишком короткий");
            response.setResult(false);
            response.setErrors(errors);
            return response;
        }
        try {
            Post post = postRepository.getOne(request.getPost_id());
            PostComment comment = new PostComment();
            PostComment parentComment = commentRepository
                    .getOne(request.getParent_id());
            if (parentComment.getId() != 0) {
                String tempText = parentComment.getText();
            }
            String currentSession = RequestContextHolder
                    .currentRequestAttributes().getSessionId();
            int id = authConfiguration.getAuths().get(currentSession);
            User user = userRepository.getOne(id);
            comment.setParentId(request.getParent_id());
            comment.setPost(post);
            comment.setText(request.getText());
            comment.setTime(new Date());
            comment.setUser(user);
            post.addPostComment(comment);
            postRepository.saveAndFlush(post);
            response.setId(commentRepository.findIdByTime(comment.getTime()));
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
     */
    @Override
    public AdditionalResponse getListOfTags(String query) {
        if (query == null)
            query = "";
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
        List<Object[]> listOfArrays = nativeQuery.getResultList();
        int totalPosts = postRepository.getActivePosts();

        AdditionalResponse response = new AdditionalResponse();
        List<AdditionalResponse> tags = new ArrayList<>();

        BigInteger currentBig = new BigInteger(listOfArrays
                .get(0)[1].toString());
        int currentCount = currentBig.intValue();
        float maxWeight = (float)(currentCount) / totalPosts;

        for (Object[] objects : listOfArrays) {
            AdditionalResponse tag = new AdditionalResponse();
            currentBig = new BigInteger(objects[1].toString());
            currentCount = currentBig.intValue();
            float currentWeight = (float) (currentCount) / totalPosts;
            if (currentWeight > maxWeight)
                maxWeight = currentWeight;
            tag.setName(objects[0].toString());
            tag.setWeight(currentWeight);
            tags.add(tag);
        }
        for (AdditionalResponse tagWithWeight : tags) {
            tagWithWeight.setWeight
                    (tagWithWeight.getWeight()/maxWeight);
        }
        response.setTags(tags);
        return response;
    }

    /**
     * Метод postModeration
     * Метод фиксирует действие модератора по посту: его утверждение или
     * отклонение
     */
    @Override
    public BasicResponse postModeration(BasicRequest request) {
        BasicResponse response = new BasicResponse();
        response.setResult(false);

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int id = authConfiguration.getAuths().get(currentSession);
        User user = userRepository.findById(id).get();

        if (user.isModerator()) {
            int postId = request.getPost_id();
            Post post = postRepository.getOne(postId);

            String decision = request.getDecision();
            if (decision.equals("accept"))
                post.setModerationStatus(PostStatus.ACCEPTED);
            else if (decision.equals("decline"))
                post.setModerationStatus(PostStatus.DECLINED);
            else
                return response;

            int moderatorId = user.getId();
            post.setModeratorId(moderatorId);

            postRepository.saveAndFlush(post);

            response.setResult(true);
            return response;
        }
        return response;
    }

    /**
     * Метод numberOfPosts
     * Метод выводит количества публикаций на каждую дату переданного в
     * параметре year года или текущего года
     *
     * @param year год в виде четырёхзначного числа, если не передан -
     *             возвращать за текущий год
     */
    @Override
    public AdditionalResponse numberOfPosts(Integer year) {
        Query nativeQuery = entityManager.createNativeQuery
                ("select distinct substr(time, 1, 4) as year from posts " +
                        "order by year asc");
        List<Object> yearObjects = nativeQuery.getResultList();
        List<Integer> years = new ArrayList<>();

        for (Object current : yearObjects) {
            BigInteger currentBig = new BigInteger(current.toString());
            years.add(currentBig.intValue());
        }

        nativeQuery = entityManager.createNativeQuery
                ("select days.day, count(days.day) from (select " +
                        "substr(time, 1, 10) as day from posts where " +
                        "substr(time, 1, 4) like ?1) as days " +
                        "group by days.day");
        String parameter;
        if (year == null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            parameter = String.valueOf(currentYear);
        }
        else
            parameter = String.valueOf(year);
        nativeQuery.setParameter(1, parameter);
        List<Object[]> postObjects = nativeQuery.getResultList();
        Map<String, Integer> posts = new HashMap<>();

        for (Object[] current : postObjects) {
            String key = current[0].toString();
            BigInteger bigIntValue = new BigInteger(current[1].toString());
            int value = bigIntValue.intValue();
            posts.put(key, value);
        }

        AdditionalResponse response = new AdditionalResponse();
        response.setYears(years);
        response.setPosts(posts);

        return response;
    }

    /**
     * Метод editProfile
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля, без изменения фото
     */
    @Override
    public Object editProfile(BasicRequest request) {
        User user = getUser(authConfiguration, userRepository);

        BasicError emailError = checkEmail
                (userRepository, request.getEmail(), user.getEmail());
        BasicError nameError = checkName(request.getName());
        BasicError passwordError = new BasicError();
        if (request.getPassword() != null)
            passwordError = checkPassword(request.getPassword());

        if ((emailError.getEmail() != null) ||
                (nameError.getName() != null) ||
                (passwordError.getPassword() != null)) {
            BasicError response = new BasicError();
            BasicError errors = new BasicError();
            response.setResult(false);
            if (emailError.getEmail() != null)
                errors.setEmail(emailError.getEmail());
            if (nameError.getName() != null)
                errors.setName(nameError.getName());
            if (passwordError.getPassword() != null)
                errors.setPassword(passwordError.getPassword());
            response.setErrors(errors);
            return response;
        }
        else {
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            if (request.getPassword() != null)
                user.setPassword(request.getPassword());
            if (request.getRemovePhoto() == 1)
                user.setPhoto(null);

            userRepository.saveAndFlush(user);

            BasicResponse response = new BasicResponse();
            response.setResult(true);
            return response;
        }
    }

    /**
     * Метод editProfileWithPhoto
     * Метод обрабатывает информацию, введённую пользователем в форму
     * редактирования своего профиля, с изменением фото
     */
    @Override
    public Object editProfileWithPhoto(BasicRequest requestWithPhoto)
            throws IOException {
        User user = getUser(authConfiguration, userRepository);

        BasicError emailError = checkEmail
                (userRepository, requestWithPhoto.getEmail(), user.getEmail());
        BasicError nameError = checkName(requestWithPhoto.getName());
        BasicError passwordError = new BasicError();
        if (requestWithPhoto.getPassword() != null)
            passwordError = checkPassword(requestWithPhoto.getPassword());
        BasicError photoError = new BasicError();
        if (requestWithPhoto.getPhoto().getSize() > 10485760)
            photoError.setPhoto("Фото слишком большое, нужно не более 10 Мб");
        if ((emailError.getEmail() != null) ||
                (nameError.getName() != null) ||
                (passwordError.getPassword() != null) ||
                (photoError.getPhoto() != null)) {
            BasicError response = new BasicError();
            BasicError errors = new BasicError();
            response.setResult(false);
            if (emailError.getEmail() != null)
                errors.setEmail(emailError.getEmail());
            if (nameError.getName() != null)
                errors.setName(nameError.getName());
            if (passwordError.getPassword() != null)
                errors.setPassword(passwordError.getPassword());
            if (photoError.getPhoto() != null)
                errors.setPhoto(photoError.getPhoto());
            response.setErrors(errors);
            return response;
        }
        else {
            user.setName(requestWithPhoto.getName());
            user.setEmail(requestWithPhoto.getEmail());
            if (requestWithPhoto.getPassword() != null)
                user.setPassword(requestWithPhoto.getPassword());

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
            String orgName = requestWithPhoto.getPhoto().getOriginalFilename();
            String filePath = realPathToUploads + orgName;

            File dest = new File(filePath);
            requestWithPhoto.getPhoto().transferTo(dest);

            BufferedImage image = ImageIO.read(dest);
            BufferedImage newImage = new BufferedImage(
                    36, 36, BufferedImage.TYPE_INT_RGB);
            int startX = 0, startY = 0, step;
            if (image.getWidth() > image.getHeight()) {
                startX = image.getWidth() / 2 - image.getHeight() / 2;
                step = image.getHeight() / 36;
            }
            else {
                startY = image.getHeight() / 2 - image.getWidth() / 2;
                step = image.getWidth() / 36;
            }
            for (int x = 0; x < 36; x++)
                for (int y = 0; y < 36; y++) {
                    int rgb = image.getRGB(
                            x * step + startX, y * step + startY);
                    newImage.setRGB(x, y, rgb);
                }
            File newFile = new File(
                    "src/main/resources/static/img/user" +
                            user.getId() + "Ava.jpg");
            ImageIO.write(newImage, "jpg", newFile);

            user.setPhoto("/img/user" + user.getId() + "Ava.jpg");

            userRepository.saveAndFlush(user);

            BasicResponse response = new BasicResponse();
            response.setResult(true);
            return response;
        }
    }

    /**
     * Метод myStatistics
     * Метод возвращает статистику постов текущего авторизованного пользователя
     */
    @Override
    public BasicResponse myStatistics() {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userId = authConfiguration.getAuths().get(currentSession);
        BasicResponse response = new BasicResponse();
        int postsCount = postRepository.getPostsCountOfUser(userId);
        if (postsCount == 0) {
            response.setPostsCount(0);
            response.setLikesCount(0);
            response.setDislikesCount(0);
            response.setViewsCount(0);
            response.setFirstPublication(0);
        }
        else {
            response.setPostsCount(postsCount);
            response.setLikesCount(postRepository
                    .getLikesCountOfUsersPosts(userId));
            response.setDislikesCount(postRepository
                    .getDisLikesCountOfUsersPosts(userId));
            response.setViewsCount(postRepository
                    .getViewsCountOfUsersPosts(userId));
            response.setFirstPublication(postRepository
                    .getFirstPostOfUser(userId).getTime()/1000);
        }
        return response;
    }

    /**
     * Метод allStatistics
     * Метод возвращает статистику  всем постам блога
     */
    @Override
    public Object allStatistics() {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userId = authConfiguration.getAuths().get(currentSession);
        if ((userRepository.isAdmin(userId) == 0) &&
                (globalSettingsRepository.statisticsIsPublic().equals("NO")))
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        else {
            BasicResponse response = new BasicResponse();
            response.setPostsCount(postRepository.getPostsCount());
            response.setLikesCount(postRepository.getLikesCount());
            response.setDislikesCount(postRepository.getDisLikesCount());
            response.setViewsCount(postRepository.getViewsCount());
            response.setFirstPublication(postRepository.getFirstPost()
                    .getTime()/1000);
            return response;
        }
    }

    /**
     * Метод getSettings
     * Метод возвращает глобальные настройки блога из таблицы global_settings
     */
    @Override
    public BasicResponse getSettings() {
        BasicResponse response = new BasicResponse();
        List <GlobalSetting> settings = globalSettingsRepository.findAll();
        for (GlobalSetting setting : settings) {
            if (setting.getCode().equals("MULTIUSER_MODE"))
                response.setMultiUserMode(
                        setting.getValue().equals("YES"));
            if (setting.getCode().equals("POST_PREMODERATION"))
                response.setPostPreModeration(
                        setting.getValue().equals("YES"));
            if (setting.getCode().equals("STATISTICS_IS_PUBLIC"))
                response.setStatisticsIsPublic(
                        setting.getValue().equals("YES"));
        }
        return response;
    }

    /**
     * Метод putSettings
     * Метод записывает глобальные настройки блога в таблицу global_settings,
     * если запрашивающий пользователь авторизован и является модератором
     */
    @Override
    public void putSettings(AdditionalRequest request) {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userId = authConfiguration.getAuths().get(currentSession);
        if (userRepository.isAdmin(userId) == 1) {
            List<GlobalSetting> settings = globalSettingsRepository.findAll();
            for (GlobalSetting globalSetting : settings) {
                if (globalSetting.getCode().equals("MULTIUSER_MODE"))
                    if (request.isMultiUserMode())
                        globalSetting.setValue("YES");
                    else
                        globalSetting.setValue("NO");
                if (globalSetting.getCode().equals("POST_PREMODERATION"))
                    if (request.isPostPreModeration())
                        globalSetting.setValue("YES");
                    else
                        globalSetting.setValue("NO");
                if (globalSetting.getCode().equals("STATISTICS_IS_PUBLIC"))
                    if (request.isStatisticsIsPublic())
                        globalSetting.setValue("YES");
                    else
                        globalSetting.setValue("NO");
            }
            globalSettingsRepository.saveAll(settings);
        }
    }

    private static User getUser(AuthConfiguration authConfiguration,
                                UserRepository userRepository) {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int id = authConfiguration.getAuths().get(currentSession);
        return userRepository.findById(id).get();
    }

    private static BasicError checkEmail
            (UserRepository userRepository,
             String requestEmail,
             String sessionEmail) {
        BasicError emailError = new BasicError();
        if ((userRepository.findByEmail(requestEmail) != null)
                && !sessionEmail.equals(requestEmail))
            emailError.setEmail("Этот e-mail уже зарегистрирован");
        return emailError;
    }

    private static BasicError checkName(String requestName) {
        BasicError nameError = new BasicError();
        if (requestName == null)
            nameError.setName("Имя указано неверно");
        return nameError;
    }

    private static BasicError checkPassword(String password) {
        BasicError passwordError = new BasicError();
        if (password.length() < 6)
            passwordError.setPassword("Пароль короче 6-ти символов");
        return passwordError;
    }
}
