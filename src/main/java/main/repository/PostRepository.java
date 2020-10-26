package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс PostRepository. Слой для работы с БД и сущностью Post
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    /**
     * Метод findPostsSortedByLikesCount
     * Метод получения постов со всей сопутствующей информацией, отсортирован-
     * ных по количеству лайков
     *
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "SELECT posts.id, " +
            "posts.is_active, posts.moderation_status, posts.moderator_id, " +
            "posts.text, posts.time, posts.title, posts.view_count, " +
            "posts.user_id FROM posts left join (SELECT post_id, " +
            "count(value) as likes from post_votes where value = 1 " +
            "group by post_id order by count(value) desc) as likes " +
            "on posts.id = likes.post_id where posts.is_active = 1 " +
            "and posts.moderation_status = 'ACCEPTED' " +
            "and posts.time < current_time() order by likes desc",
            countQuery = "SELECT count(*) Posts",
            nativeQuery = true)
    Page<Post> findPostsSortedByLikesCount(Pageable pageable);

    /**
     * Метод findPostsSortedByCommentsCount
     * Метод получения постов со всей сопутствующей информацией, отсортирован-
     * ных по количеству комментариев
     *
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "SELECT posts.id, posts.is_active, posts.moderation_status, " +
            "posts.moderator_id, posts.text, posts.time, posts.title, " +
            "posts.view_count, posts.user_id FROM posts left join " +
            "(SELECT post_id, count(id) as comments from post_comments " +
            "group by post_id order by count(id) desc) as comments " +
            "on posts.id = comments.post_id where posts.is_active = 1 " +
            "and posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() order by comments desc",
            countQuery = "SELECT count(*) Posts",
            nativeQuery = true)
    Page<Post> findPostsSortedByCommentsCount(Pageable pageable);

    /**
     * Метод findEarlyPosts
     * Метод получения постов со всей сопутствующей информацией, отсортирован-
     * ных по времени создания от ранних к поздним
     *
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "SELECT * FROM posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time() " +
            "order by time",
            nativeQuery = true)
    Page<Post> findEarlyPosts(Pageable pageable);

    /**
     * Метод findRecentPosts
     * Метод получения постов со всей сопутствующей информацией, отсортирован-
     * ных по времени создания от поздних к ранним
     *
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "SELECT * FROM posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time() " +
            "order by time desc",
            nativeQuery = true)
    Page<Post> findRecentPosts(Pageable pageable);

    /**
     * Метод searchForPostsByQuery
     * Метод возвращает посты, соответствующие поисковому запросу
     *
     * @param query поисковый запрос
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "SELECT * FROM posts where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and (posts.text like concat('%',:query,'%') " +
            "or posts.title like concat('%',:query,'%'))",
            nativeQuery = true)
    List<Post> searchForPostsByQuery
            (@Param("query") String query, Pageable pageable);

    /**
     * Метод getPostsByDate
     * Выводит посты за указанную дату, переданную в запросе
     *
     * @param dateBefore к дате в формате "2019-10-15" добавлены часы,
     *                   минуты, секунды и миллисекунды: 00:00:00.000000
     * @param dateAfter к дате в формате "2019-10-16" добавлены часы,
     *      *            минуты, секунды и миллисекунды: 00:00:00.000000
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time > :dateBefore " +
            " and time < :dateAfter and time < current_time()",
            nativeQuery = true)
    List<Post> getPostsByDate(@Param("dateBefore") String dateBefore,
                              @Param("dateAfter") String dateAfter,
                              Pageable pageable);

    /**
     * Метод getPostsByTag
     * Метод выводит список постов, привязанных к тэгу, который был передан
     *
     * @param tag тэг, по которому нужно вывести все посты
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select tag2post.id, " +
            "tag2post.post_id, tags.name as tag_name from tag2post " +
            "join tags on tag2post.tag_id = tags.id) as temp on " +
            "posts.id = temp.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and temp.tag_name = :query",
            nativeQuery = true)
    List<Post> getPostsByTag(@Param("query") String tag, Pageable pageable);

    /**
     * Метод getNewPosts
     * Метод выводит все посты со статусом "NEW"
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'NEW'",
            nativeQuery = true)
    List<Post> getNewPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getDeclinedPosts
     * Метод выводит все посты со статусом "DECLINED"
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'DECLINED'",
            nativeQuery = true)
    List<Post> getDeclinedPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getAcceptedPosts
     * Метод выводит все посты со статусом "ACCEPTED"
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED'",
            nativeQuery = true)
    List<Post> getAcceptedPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getMyInactivePosts
     * Метод выводит только те неактивные посты, которые создал я
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 0",
            nativeQuery = true)
    List<Post> getMyInactivePosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getMyPendingPosts
     * Метод выводит только те активные, ожидающие утверждения модератором
     * посты, которые создал я
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'NEW'",
            nativeQuery = true)
    List<Post> getMyPendingPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getMyDeclinedPosts
     * Метод выводит только те активные, отклонённые модератором
     * посты, которые создал я
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'DECLINED'",
            nativeQuery = true)
    List<Post> getMyDeclinedPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getMyPublishedPosts
     * Метод выводит только те активные, опубликованные по итогам модерации
     * посты, которые создал я
     *
     * @param name имя пользователя, чьи посты выводим
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED'",
            nativeQuery = true)
    List<Post> getMyPublishedPosts
            (@Param("query") String name, Pageable pageable);

    /**
     * Метод getPost
     * Метод выводит данные конкретного поста для отображения на странице
     *
     * @param id поста, который мы хотим ищем
     */
    @Query(value = "select * from posts where id = :query and " +
            "is_active = 1 and moderation_status = 'ACCEPTED' and " +
            "time <= current_time()", nativeQuery = true)
    Post getPost(@Param("query") int id);

    /**
     * Метод getActivePosts
     * Метод выводит количество активных публикаций, утверждённых модератором
     * со временем публикации, не превышающем текущее время
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    int getActivePosts();

    /**
     * Метод getCountOfPostsForModeration
     * Метод выводит количество постов необходимых для проверки модераторами
     *
     * @param moderatorId
     */
    @Query(value = "select count(*) from (select * from posts where " +
            "moderation_status = 'NEW' and moderator_id = :query or " +
            "moderator_id = 0) as new_posts", nativeQuery = true)
    int getCountOfPostsForModeration(@Param("query") int moderatorId);
}