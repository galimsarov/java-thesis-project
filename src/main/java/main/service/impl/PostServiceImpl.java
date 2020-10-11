package main.service.impl;

import main.mapper.PostResponseMapper;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.model.helper.PostStatus;
import main.repository.PostRepository;
import main.repository.TagRepository;
import main.repository.UserRepository;
import main.request.PostRequest;
import main.response.*;
import main.service.PostService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Класс PostServiceImpl
 * Сервисный слой для работы с постами
 *
 * @version 1.0
 */

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

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
        Pageable pageable = PageRequest.of(offset, limit);
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
        answer.setCount(postDTOList.size());
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
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                searchForPostsByQuery(query, pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postDTOList.size());
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
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                getPostsByDate(dateBefore(date), dateAfter(date), pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postDTOList.size());
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
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList = postRepository.
                getPostsByTag(tag, pageable);
        for (Post post : postList) {
            PostResponse postDTO = mapper.postToPostDTO(post);
            postDTOList.add(postDTO);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postDTOList.size());
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
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList;

        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        String name = auth.getName();

        switch (status) {
            case "new":
                postList = postRepository.getNewPosts(name, pageable);
                break;
            case "declined":
                postList = postRepository.getDeclinedPosts(name, pageable);
                break;
            default:
                postList = postRepository.getAcceptedPosts(name, pageable);
        }
        for (Post post : postList) {
            PostResponse postResponse = mapper.postToPostDTO(post);
            postResponseList.add(postResponse);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postResponseList.size());
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
        Pageable pageable = PageRequest.of(offset, limit);
        List<Post> postList;

        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        String name = auth.getName();

        switch (status) {
            case "inactive":
                postList = postRepository.getMyInactivePosts(name, pageable);
                break;
            case "pending":
                postList = postRepository.getMyPendingPosts(name, pageable);
                break;
            case "declined":
                postList = postRepository.getMyDeclinedPosts(name, pageable);
                break;
            default:
                postList = postRepository.getMyPublishedPosts(name, pageable);
        }
        for (Post post : postList) {
            PostResponse postResponse = mapper.postToPostDTO(post);
            postResponseList.add(postResponse);
        }
        ListOfPostsResponse answer = new ListOfPostsResponse();
        answer.setCount(postResponseList.size());
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

        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        User user = userRepository.findByName(auth.getName());

        Post post = new Post();
        post = createNewPost(postRequest, user, post);
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

        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        User user = userRepository.findByName(auth.getName());

        if ((user.getId() != id) &&
                (userRepository.isAdmin(user.getName()) == 0)) {
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
        post = createNewPost(postRequest, user, post);
        if (user.getId() == id)
            post.setModerationStatus(PostStatus.NEW);

        postRepository.saveAndFlush(post);

        SuccessfullyAddedPost addedPost = new SuccessfullyAddedPost();
        addedPost.setResult(true);
        return addedPost;
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
        Authentication auth = SecurityContextHolder.getContext().
                getAuthentication();
        String name = auth.getName();
        if (name.equals("anonymousUser"))
            return post.getViewCount();
        if (userRepository.isAdmin(name) == 1)
            return post.getViewCount();
        if (post.getUser().getName().equals(name))
            return post.getViewCount();
        return post.getViewCount() + 1;
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
        post.setViewCount(0);
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