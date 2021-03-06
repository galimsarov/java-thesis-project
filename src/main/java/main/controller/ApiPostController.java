package main.controller;

import main.model.request.others.PostRequest;
import main.model.request.postids.PostIdRequest;
import main.model.response.others.ThePosts;
import main.model.response.results.ResultResponse;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Класс ApiPostController
 * REST-контроллер, обрабатывает все запросы /api/post/*
 *
 * @version 1.2
 */

@RestController
@RequestMapping("/api/post")
public class ApiPostController {
    @Autowired
    PostService postService;

    /**
     * Метод listOfPosts
     * Метод получения постов со всей сопутствующей информацией для главной страницы
     * GET запрос /api/post
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param mode режим вывода (сортировка): popular, best, early либо recent, если
     *             другие значения не были указаны
     */
    @GetMapping
    public ThePosts listOfPosts(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String mode) {
        return postService.getListOfPostResponse(offset, limit, mode);
    }

    /**
     * Метод searchForPosts
     * Метод возвращает посты, соответствующие поисковому запросу - строке query
     * GET запрос /api/post/search
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param query поисковый запрос
     */
    @GetMapping("/search")
    public ThePosts searchForPosts(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String query) {
        return postService.searchForPostResponse(offset, limit, query);
    }

    /**
     * Метод postsByDate
     * Выводит посты за указанную дату, переданную в запросе в параметре date
     * GET запрос /api/post/byDate
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param date дата в формате "2019-10-15"
     */
    @GetMapping("/byDate")
    public ThePosts postsByDate(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    /**
     * Метод postsByTag
     * Метод выводит список постов, привязанных к тэгу, который был передан
     * GET запрос /api/post/byTag
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param tag тэг, по которому нужно вывести все посты
     */
    @GetMapping("/byTag")
    public ThePosts postsByTag(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    /**
     * Метод listOfPostsForModeration
     * Метод выводит все посты, которые требуют модерационных действий
     * GET запрос /api/post/moderation
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param status статус модерации: new, declined или accepted
     */
    @GetMapping("/moderation")
    public ThePosts listOfPostsForModeration(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String status) {
        return postService.getPostsForModeration(offset, limit, status);
    }

    /**
     * Метод listOfMyPosts
     * Метод выводит только те посты, которые создал я
     * GET запрос /api/post/my
     *
     * @param offset сдвиг от 0 для постраничного вывода
     * @param limit количество постов, которое надо вывести
     * @param status статус модерации: inactive, pending, declined или published
     */
    @GetMapping("/my")
    public ThePosts listOfMyPosts(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam String status) {
        return postService.getMyPosts(offset, limit, status);
    }

    /**
     * Метод getPost
     * Метод выводит данные конкретного поста для отображения на странице поста
     * GET запрос /api/post/{id}
     *
     * @param id поста, который мы хотим найти
     */
    @GetMapping("/{id}")
    public Object getPost(
            @PathVariable(value = "id") int id) {
        return postService.getPost(id);
    }

    /**
     * Метод addPost
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации
     * POST запрос /api/post
     */
    @PostMapping
    public Object addPost(@RequestBody PostRequest request) {
        return postService.addPost(request);
    }

    /**
     * Метод editPost
     * Метод изменяет данные поста, которые пользователь ввёл в форму публикации
     * POST запрос /api/post
     *
     * @param id поста, который мы хотим изменить
     */
    @PutMapping("/{id}")
    public Object editPost(@PathVariable(value = "id") int id,
                           @RequestBody PostRequest request) {
        return postService.editPost(id, request);
    }

    /**
     * Метод like
     * Метод сохраняет в таблицу post_votes лайк текущего авторизованного
     * пользователя
     * POST запрос /api/post/like
     */
    @PostMapping("/like")
    public ResultResponse like(@RequestBody PostIdRequest request) {
        return postService.like(request);
    }

    /**
     * Метод dislike
     * Метод сохраняет в таблицу post_votes дизлайк текущего авторизованного
     * пользователя
     * POST запрос /api/post/dislike
     */
    @PostMapping("/dislike")
    public ResultResponse dislike(@RequestBody PostIdRequest request) {
        return postService.dislike(request);
    }
}
