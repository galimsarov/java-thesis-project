package main.service.impl;

import lombok.RequiredArgsConstructor;
import main.config.AuthConfiguration;
import main.mapper.PostResponseMapper;
import main.model.Post;
import main.model.PostVote;
import main.model.Tag;
import main.model.User;
import main.model.helper.PostStatus;
import main.repository.PostRepository;
import main.repository.PostVoteRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import main.request.PostRequest;
import main.request.PostVoteRequest;
import main.response.*;
import main.service.PostService;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;

/**
 * Класс PostServiceImpl
 * Сервисный слой для работы с постами
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostVoteRepository postVoteRepository;
    private final AuthConfiguration authConfiguration;

    /**
     * Метод getListOfPostResponse
     * Метод получения постов со всей сопутствующей информацией
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param mode режим вывода (сортировка): recent, popular, best или early
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse getListOfPostResponse
                (int offset, int limit, String mode) {
        List<PostResponse> postDTOList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper(PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        Page<Post> postPage;
        switch (mode) {
            case "best":
                postPage = postRepository.
                        findPostsSortedByLikesCount(pageable);
                break;
            case "popular":
                postPage = postRepository.
                        findPostsSortedByCommentsCount(pageable);
                break;
            case "early":
                postPage = postRepository.
                        findEarlyPosts(pageable);
                break;
            default:
                postPage = postRepository.
                        findRecentPosts(pageable);
        }
        for (Post post : postPage) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postRepository.findCountOfPosts());
        answer.setPosts(postDTOList);
        return answer;
    }

    /**
     * Метод searchForPostResponse
     * Метод возвращает посты, соответствующие поисковому запросу - строке query
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param query поисковый запрос
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse searchForPostResponse
            (int offset, int limit, String query) {
        List<PostResponse> postDTOList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper
                (PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        List<Post> postList = postRepository.
                searchForPostsByQuery(query, pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postRepository.getCountOfPostsByQuery(query));
        answer.setPosts(postDTOList);
        return answer;
    }

    /**
     * Метод getPostsByDate
     * Выводит посты за указанную дату, переданную в запросе в параметре date
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param date дата в формате "2019-10-15"
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse getPostsByDate
            (int offset, int limit, String date) {
        List<PostResponse> postDTOList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper(PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        List<Post> postList = postRepository.
                getPostsByDate(dateBefore(date), dateAfter(date), pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postRepository
                .getCountOfPostsByDate(dateBefore(date), dateAfter(date)));
        answer.setPosts(postDTOList);
        return answer;
    }

    /**
     * Метод getPostsByTag
     * Метод выводит список постов, привязанных к тэгу, который был передан
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param tag тэг, по которому нужно вывести все посты
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse getPostsByTag
            (int offset, int limit, String tag) {
        List<PostResponse> postDTOList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper(PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        List<Post> postList = postRepository.
                getPostsByTag(tag, pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postRepository.getCountOfPostsByTag(tag));
        answer.setPosts(postDTOList);
        return answer;
    }

    /**
     * Метод getPostsForModeration
     * Метод выводит все посты, которые требуют модерационных действий
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param status статус модерации: new, declined или accepted
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse getPostsForModeration
            (int offset, int limit, String status) {
        List<PostResponse> postResponseList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper(PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        List<Post> postList;
        int count;

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int id = authConfiguration.getAuths().get(currentSession);

        switch (status) {
            case "new":
                postList = postRepository.getNewPosts(id, pageable);
                count = postRepository.getCountOfNewPosts(id);
                break;
            case "declined":
                postList = postRepository.getDeclinedPosts(id, pageable);
                count = postRepository.getCountOfDeclinedPosts(id);
                break;
            default:
                postList = postRepository.getAcceptedPosts(id, pageable);
                count = postRepository.getCountOfAcceptedPosts(id);
        }
        for (Post post : postList) {
            PostResponse postResponse = mapper.postToPostDTO(post);
            postResponseList.add(postResponse);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(count);
        answer.setPosts(postResponseList);
        return answer;
    }

    /**
     * Метод getMyPosts
     * Метод выводит только те посты, которые создал я
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param status статус модерации: inactive, pending, declined или published
     * @see ListOfPostsResponse
     */
    @Override
    public ListOfPostsResponse getMyPosts
            (int offset, int limit, String status) {
        List<PostResponse> postResponseList = new ArrayList<>();
        PostResponseMapper mapper = Mappers.getMapper
                (PostResponseMapper.class);
        Pageable pageable = PageRequest.of(offset/10, limit);
        List<Post> postList;
        int count;

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int id = authConfiguration.getAuths().get(currentSession);

        switch (status) {
            case "inactive":
                postList = postRepository.getMyInactivePosts(id, pageable);
                count = postRepository.getCountOfMyInactivePosts(id);
                break;
            case "pending":
                postList = postRepository.getMyPendingPosts(id, pageable);
                count = postRepository.getCountOfMyPendingPosts(id);
                break;
            case "declined":
                postList = postRepository.getMyDeclinedPosts(id, pageable);
                count = postRepository.getCountOfMyDeclinedPosts(id);
                break;
            default:
                postList = postRepository.getMyPublishedPosts(id, pageable);
                count = postRepository.getCountOfMyPublishedPosts(id);
        }
        for (Post post : postList) {
            PostResponse postResponse = mapper.postToPostDTO(post);
            postResponseList.add(postResponse);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(count);
        answer.setPosts(postResponseList);
        return answer;
    }

    /**
     * Метод getPost
     * Метод выводит данные конкретного поста для отображения на странице поста
     *
     * @param id пост, который мы хотим ищем
     * @see SpecificPostResponse
     */
    @Override
    public SpecificPostResponse getPost(int id) {
        PostResponseMapper mapper = Mappers.getMapper
                (PostResponseMapper.class);
        Post post = postRepository.getPost(id);
        if (post == null)
            return null;
        else {
            post.setViewCount(checkViewCount(post));
            postRepository.saveAndFlush(post);
            return mapper.postToSpecificPost(post);
        }
    }

    /**
     * Метод addPost
     * Метод отправляет данные поста, которые пользователь ввёл в форму
     *
     * @see PostRequest
     */
    @Override
    public AbstractResponse addPost(PostRequest postRequest) {
        ErrorAddingPost errorResponse = checkPostData(postRequest);
        if (!errorResponse.isResult())
            return errorResponse;

        if (postRequest.getTimestamp() < (new Date()).getTime()) {
            long newTimeStamp = (new Date()).getTime();
            postRequest.setTimestamp(newTimeStamp);
        }

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int id = authConfiguration.getAuths().get(currentSession);

        Post post = new Post();
        createNewPost(postRequest, userRepository.getOne(id), post);
        post.setModerationStatus(PostStatus.NEW);

        postRepository.saveAndFlush(post);

        SuccessfullyAddedPost addedPost = new SuccessfullyAddedPost();
        addedPost.setResult(true);
        return addedPost;
    }

    /**
     * Метод editPost
     * Метод изменяет данные поста, которые пользователь ввёл в форму публикации
     *
     * @param id поста, который мы хотим изменить
     * @see PostRequest
     */
    @Override
    public AbstractResponse editPost(int id, PostRequest postRequest) {
        ErrorAddingPost errorResponse = checkPostData(postRequest);
        if (!errorResponse.isResult())
            return errorResponse;

        if (postRequest.getTimestamp() < (new Date()).getTime()) {
            long newTimeStamp = (new Date()).getTime();
            postRequest.setTimestamp(newTimeStamp);
        }

        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userIdFromContext = authConfiguration
                .getAuths().get(currentSession);
        User user = userRepository.getOne(userIdFromContext);

        if ((userIdFromContext != id) &&
                (userRepository.isAdmin(userIdFromContext) == 0)) {
            ErrorAddingPost errorAuthResponse = new ErrorAddingPost();
            errorAuthResponse.setResult(false);
            TextError textError = new TextError();
            textError.setText("Вы не являетесь модератором и это не ваш пост");
            List<AbstractError> errors = new ArrayList<>();
            errors.add(textError);
            errorAuthResponse.setErrors(errors);
            return errorAuthResponse;
        }

        Post post = postRepository.getOne(id);
        createNewPost(postRequest, user, post);
        if (user.getId() == id)
            post.setModerationStatus(PostStatus.NEW);

        postRepository.saveAndFlush(post);

        SuccessfullyAddedPost addedPost = new SuccessfullyAddedPost();
        addedPost.setResult(true);
        return addedPost;
    }

    /**
     * Метод like
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного
     * пользователя
     *
     * @see main.request.PostVoteRequest
     */
    @Override
    public AbstractResponse like(PostVoteRequest request) {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userId = authConfiguration.getAuths().get(currentSession);
        SuccessfullyAddedPost response = new SuccessfullyAddedPost();

        PostVote postVote = postVoteRepository
                .getPostVoteByPostAndUser(request.getPostId(), userId);
        if (postVote == null) {
            PostVote newPostVote = new PostVote();
            newPostVote.setPost(postRepository.getPost(request.getPostId()));
            newPostVote.setTime(new Date());
            newPostVote.setUser(userRepository.getOne(userId));
            newPostVote.setValue(1);
            postVoteRepository.saveAndFlush(newPostVote);
            response.setResult(true);
        }
        else {
            if (postVote.getValue() == 1)
                response.setResult(false);
            else {
                postVote.setValue(1);
                postVote.setTime(new Date());
                postVoteRepository.saveAndFlush(postVote);
                response.setResult(true);
            }
        }
        return response;
    }

    /**
     * Метод dislike
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного
     * пользователя
     *
     * @see main.request.PostVoteRequest
     */
    @Override
    public AbstractResponse dislike(PostVoteRequest request) {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        int userId = authConfiguration.getAuths().get(currentSession);
        SuccessfullyAddedPost response = new SuccessfullyAddedPost();

        PostVote postVote = postVoteRepository
                .getPostVoteByPostAndUser(request.getPostId(), userId);
        if (postVote == null) {
            PostVote newPostVote = new PostVote();
            newPostVote.setPost(postRepository.getPost(request.getPostId()));
            newPostVote.setTime(new Date());
            newPostVote.setUser(userRepository.getOne(userId));
            newPostVote.setValue(-1);
            postVoteRepository.saveAndFlush(newPostVote);
            response.setResult(true);
        }
        else {
            if (postVote.getValue() == -1)
                response.setResult(false);
            else {
                postVote.setValue(-1);
                postVote.setTime(new Date());
                postVoteRepository.saveAndFlush(postVote);
                response.setResult(true);
            }
        }
        return response;
    }

    private String dateBefore(String date) {
        return date + " 00:00:00.000000";
    }

    private String dateAfter(String date) {
        return date.substring(0, 8) +
                (Integer.parseInt(date.substring(8, 10)) + 1) +
                " 00:00:00.000000";
    }

    private int checkViewCount(Post post) {
        String currentSession = RequestContextHolder
                .currentRequestAttributes().getSessionId();
        try {
            int id = authConfiguration.getAuths().get(currentSession);
            if (userRepository.isAdmin(id) == 1)
                return post.getViewCount();
            if (post.getUser().getId() == id)
                return post.getViewCount();
            return post.getViewCount() + 1;
        }
        catch (NullPointerException e) {
            return post.getViewCount() + 1;
        }
    }

    private ErrorAddingPost checkPostData(PostRequest postRequest) {
        ErrorAddingPost response = new ErrorAddingPost();
        response.setResult(true);
        String titleError = "", textError = "";
        if (postRequest.getTitle().length() == 0)
            titleError = "Заголовок не установлен";
        else
        if (postRequest.getTitle().length() < 3)
            titleError = "Текст заголовка слишком короткий";
        if (postRequest.getText().length() == 0)
            textError = "Публикация не установлена";
        else
        if (postRequest.getText().length() < 50)
            textError = "Текст публикации слишком короткий";
        if ((textError.length() != 0) || (titleError.length() != 0)) {
            List<AbstractError> errors = new ArrayList<>();
            if (titleError.length() != 0) {
                TitleError theTitleError = new TitleError();
                theTitleError.setTitle(titleError);
                errors.add(theTitleError);
            }
            if (textError.length() != 0) {
                TextError theTextError = new TextError();
                theTextError.setText(textError);
                errors.add(theTextError);
            }
            response.setResult(false);
            response.setErrors(errors);
        }
        return response;
    }

    private Post createNewPost(PostRequest postRequest, User user, Post post) {
        post.setActive(postRequest.getActive() == 1);
        post.setTime(new Date(postRequest.getTimestamp()));
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setUser(user);

        List<String> namesOfCurrentTags = tagRepository.findNamesOfTags();
        Set<Tag> newTags = new HashSet<>();
        for (String nameOfTagFromRequest : postRequest.getTags()) {
            Tag newTag = new Tag();
            if (namesOfCurrentTags.contains(nameOfTagFromRequest))
                newTag = tagRepository.findTagByName(nameOfTagFromRequest);
            else
                newTag.setName(nameOfTagFromRequest);
            newTags.add(newTag);
        }
        post.setTagSet(newTags);
        return post;
    }
}