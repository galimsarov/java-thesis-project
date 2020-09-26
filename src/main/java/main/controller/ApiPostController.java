package main.controller;

import main.request.PostRequest;
import main.response.AbstractResponse;
import main.response.ListOfPostsResponse;
import main.response.SpecificPostResponse;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Класс ApiPostController
 * REST-контроллер для работы с постами
 *
 * @version 1.0
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
     * @see ListOfPostsResponse
     */
    @GetMapping
    public ListOfPostsResponse listOfPosts(
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
     * @see ListOfPostsResponse
     */
    @GetMapping("/search")
    public ListOfPostsResponse searchForPosts(
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
     * @see ListOfPostsResponse
     */
    @GetMapping("/byDate")
    public ListOfPostsResponse postsByDate(
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
     * @see ListOfPostsResponse
     */
    @GetMapping("/byTag")
    public ListOfPostsResponse postsByTag(
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
     * @see ListOfPostsResponse
     */
    @GetMapping("/moderation")
    public ListOfPostsResponse listOfPostsForModeration(
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
     * @see ListOfPostsResponse
     */
    @GetMapping("/my")
    public ListOfPostsResponse listOfMyPosts(
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
     * @param id пост, который мы хотим ищем
     * @see SpecificPostResponse
     */
    @GetMapping("/{id}")
    public Object getPost(@PathVariable(value = "id") int id) {
        SpecificPostResponse response =
                postService.getPost(id);
        return Objects.requireNonNullElseGet
                (response, () -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    /**
     * Метод addPost
     * Метод отправляет данные поста, которые пользователь ввёл в форму публикации
     * POST запрос /api/post
     *
     * @see PostRequest
     */
    @PostMapping
    public AbstractResponse addPost(@RequestBody PostRequest postRequest) {
        return postService.addPost(postRequest);
    }
}
