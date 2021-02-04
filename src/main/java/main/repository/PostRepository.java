package main.repository;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
     * Метод findCountOfPosts
     * Метод получения количества постов, которое доступно с учётом всех
     * фильтров, параметров доступности
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    int findCountOfPosts();

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
     * Метод getCountOfPostsByQuery
     * Метод получения количества постов, соответствующих поисковому запросу
     *
     * @param query поисковый запрос
     */
    @Query(value = "select count(*) from posts where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and (posts.text like concat('%',:query,'%') or " +
            "posts.title like concat('%',:query,'%'))", nativeQuery = true)
    int getCountOfPostsByQuery(@Param("query") String query);

    /**
     * Метод getPostsByDate
     * Выводит посты за указанную дату, переданную в запросе
     *
     * @param dateBefore к дате в формате "2019-10-15" добавлены часы,
     *                  минуты, секунды и миллисекунды: 00:00:00.000000
     * @param dateAfter к дате в формате "2019-10-16" добавлены часы,
     *                  минуты, секунды и миллисекунды: 00:00:00.000000
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
     * Метод getCountOfPostsByDate
     * Выводит количество постов за указанную дату, переданную в запросе
     *
     * @param dateBefore к дате в формате "2019-10-15" добавлены часы,
     *                  минуты, секунды и миллисекунды: 00:00:00.000000
     * @param dateAfter к дате в формате "2019-10-16" добавлены часы,
     *                  минуты, секунды и миллисекунды: 00:00:00.000000
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time > :dateBefore and time < " +
            ":dateAfter and time < current_time()", nativeQuery = true)
    int getCountOfPostsByDate(@Param("dateBefore") String dateBefore,
                              @Param("dateAfter") String dateAfter);

    /**
     * Метод getPostsByTag
     * Метод выводит список постов, привязанных к тэгу, который был передан
     *
     * @param tag тэг, по которому нужно вывести все посты
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select posts.id, posts.is_active, posts.moderation_status, " +
            "posts.moderator_id, posts.text, posts.time, posts.title, " +
            "posts.view_count, posts.user_id from posts join (select " +
            "tag2post.tag_id, tag2post.post_id, tags.name as tag_name from " +
            "tag2post join tags on tag2post.tag_id = tags.id) as temp on " +
            "posts.id = temp.post_id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and temp.tag_name = :query",
            nativeQuery = true)
    List<Post> getPostsByTag(@Param("query") String tag, Pageable pageable);

    /**
     * Метод getCountOfPostsByTag
     * Метод выводит количество постов, привязанных к тэгу, который был передан
     *
     * @param tag тэг, по которому нужно вывести все посты
     */
    @Query(value = "select count(*) from (select posts.id, posts.is_active, " +
            "posts.moderation_status, posts.moderator_id, posts.text, " +
            "posts.time, posts.title, posts.view_count, posts.user_id from " +
            "posts join (select tag2post.tag_id, tag2post.post_id, tags.name " +
            "as tag_name from tag2post join tags on tag2post.tag_id = tags.id) " +
            "as temp on posts.id = temp.post_id where temp.tag_name = :query) " +
            "as new_table where new_table.is_active = 1 and " +
            "new_table.moderation_status = 'ACCEPTED' and new_table.time < " +
            "current_time()", nativeQuery = true)
    int getCountOfPostsByTag(@Param("query") String tag);

    /**
     * Метод getNewPosts
     * Метод выводит все посты со статусом "NEW"
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "posts.moderation_status = 'NEW' and (moderator_id = :query or " +
            "moderator_id = 0)", nativeQuery = true)
    List<Post> getNewPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfNewPosts
     * Метод выводит количество постов со статусом "NEW" для постраничного вывода
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "posts.moderation_status = 'NEW' and (moderator_id = :query or " +
            "moderator_id = 0)", nativeQuery = true)
    int getCountOfNewPosts(@Param("query") int id);

    /**
     * Метод getDeclinedPosts
     * Метод выводит все посты со статусом "DECLINED"
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "posts.moderation_status = 'DECLINED' and (moderator_id = :query " +
            "or moderator_id = 0)", nativeQuery = true)
    List<Post> getDeclinedPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfDeclinedPosts
     * Метод выводит количество постов со статусом "DECLINED" для постраничного
     * вывода
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "posts.moderation_status = 'DECLINED' and (moderator_id = :query " +
            "or moderator_id = 0)", nativeQuery = true)
    int getCountOfDeclinedPosts(@Param("query") int id);

    /**
     * Метод getAcceptedPosts
     * Метод выводит все посты со статусом "ACCEPTED"
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and (moderator_id = :query " +
            "or moderator_id = 0)", nativeQuery = true)
    List<Post> getAcceptedPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfAcceptedPosts
     * Метод выводит количество постов со статусом "ACCEPTED" для постраничного
     * вывода
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and (moderator_id = :query " +
            "or moderator_id = 0)", nativeQuery = true)
    int getCountOfAcceptedPosts(@Param("query") int id);

    /**
     * Метод getMyInactivePosts
     * Метод выводит только те неактивные посты, которые создал я
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 0 and user_id = :query",
            nativeQuery = true)
    List<Post> getMyInactivePosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfMyInactivePosts
     * Метод выводит количество неактивных постов, которые создал я
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 0 and " +
            "user_id = :query", nativeQuery = true)
    int getCountOfMyInactivePosts(@Param("query") int id);

    /**
     * Метод getMyPendingPosts
     * Метод выводит только те активные, ожидающие утверждения модератором
     * посты, которые создал я
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "moderation_status = 'NEW' and user_id = :query", nativeQuery = true)
    List<Post> getMyPendingPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfMyPendingPosts
     * Метод выводит количество активных постов, ожидающих утверждения
     * модератором постов, которые создал я
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'NEW' and user_id = :query", nativeQuery = true)
    int getCountOfMyPendingPosts(@Param("query") int id);

    /**
     * Метод getMyDeclinedPosts
     * Метод выводит только те активные, отклонённые модератором
     * посты, которые создал я
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "moderation_status = 'DECLINED' and user_id = :query",
            nativeQuery = true)
    List<Post> getMyDeclinedPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfMyDeclinedPosts
     * Метод выводит количество активных, отклонённых модератором постов,
     * которые создал я
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'DECLINED' and user_id = :query",
            nativeQuery = true)
    int getCountOfMyDeclinedPosts(@Param("query") int id);

    /**
     * Метод getMyPublishedPosts
     * Метод выводит только те активные, опубликованные по итогам модерации
     * посты, которые создал я
     *
     * @param id
     * @param pageable параметры вывода на страницу
     */
    @Query(value = "select * from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and user_id = :query",
            nativeQuery = true)
    List<Post> getMyPublishedPosts(@Param("query") int id, Pageable pageable);

    /**
     * Метод getCountOfMyPublishedPosts
     * Метод выводит количество активных, опубликованных по итогам модерации
     * постов, которые создал я
     *
     * @param id
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and user_id = :query",
            nativeQuery = true)
    int getCountOfMyPublishedPosts(@Param("query") int id);

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

    /**
     * Метод getPostsCountOfUser
     * Метод выводит количество постов, у которых user
     * является автором, доступных для чтения
     *
     * @param userId
     */
    @Query(value = "select count(*) from posts where user_id = :query and " +
            "is_active = 1 and moderation_status = 'ACCEPTED' and time " +
            "< current_time()", nativeQuery = true)
    int getPostsCountOfUser(@Param("query") int userId);

    /**
     * Метод getLikesCountOfUsersPosts
     * Метод выводит количество лайков постов для всех публикаций, у которых user
     * является автором, доступных для чтения
     *
     * @param userId
     */
    @Query(value = "select count(*) from posts join (select value, post_id " +
            "from post_votes) as temp_votes on posts.id = temp_votes.post_id " +
            "where user_id = :query and is_active = 1 and moderation_status " +
            "= 'ACCEPTED' and time < current_time() and value = 1",
            nativeQuery = true)
    int getLikesCountOfUsersPosts(@Param("query") int userId);

    /**
     * Метод getDisLikesCountOfUsersPosts
     * Метод выводит количество дизлайков постов для всех публикаций, у которых
     * user является автором, доступных для чтения
     *
     * @param userId
     */
    @Query(value = "select count(*) from posts join (select value, post_id " +
            "from post_votes) as temp_votes on posts.id = temp_votes.post_id " +
            "where user_id = :query and is_active = 1 and moderation_status " +
            "= 'ACCEPTED' and time < current_time() and value = -1",
            nativeQuery = true)
    int getDisLikesCountOfUsersPosts(@Param("query") int userId);

    /**
     * Метод getViewsCountOfUsersPosts
     * Метод выводит количество просмотров постов для всех публикаций, у которых
     * user является автором, доступных для чтения
     *
     * @param userId
     */
    @Query(value = "select sum(view_count) from posts where user_id = :query " +
            "and is_active = 1 and moderation_status = 'ACCEPTED' and time < " +
            "current_time()", nativeQuery = true)
    int getViewsCountOfUsersPosts(@Param("query") int userId);

    /**
     * Метод getFirstPostOfUser
     * Метод выводит дату первого поста, у которого user является автором,
     * доступного для чтения
     *
     * @param userId
     */
    @Query(value = "select min(time) from posts where user_id = :query and " +
            "is_active = 1 and moderation_status = 'ACCEPTED' and time < " +
            "current_time()",nativeQuery = true)
    Date getFirstPostOfUser(@Param("query") int userId);

    /**
     * Метод getPostsCount
     * Метод выводит количество постов, доступных для чтения
     */
    @Query(value = "select count(*) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    int getPostsCount();

    /**
     * Метод getLikesCount
     * Метод выводит количество лайков постов, доступных для чтения
     */
    @Query(value = "select count(*) from posts join (select value, post_id " +
            "from post_votes) as temp_votes on posts.id = temp_votes.post_id " +
            "where is_active = 1 and moderation_status = 'ACCEPTED' and time " +
            "< current_time() and value = 1", nativeQuery = true)
    int getLikesCount();

    /**
     * Метод getDisLikesCount
     * Метод выводит количество дизлайков постов, доступных для чтения
     */
    @Query(value = "select count(*) from posts join (select value, post_id " +
            "from post_votes) as temp_votes on posts.id = temp_votes.post_id " +
            "where is_active = 1 and moderation_status = 'ACCEPTED' and time " +
            "< current_time() and value = -1", nativeQuery = true)
    int getDisLikesCount();

    /**
     * Метод getViewsCount
     * Метод выводит количество просмотров постов, доступных для чтения
     */
    @Query(value = "select sum(view_count) from posts where is_active = 1 " +
            "and moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    int getViewsCount();

    /**
     * Метод getFirstPost
     * Метод выводит дату первого поста, доступного для чтения
     */
    @Query(value = "select min(time) from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    Date getFirstPost();

    @Query(value = "select substr(time, 1, 10) as day from posts where " +
            "substr(time, 1, 4) like :query", nativeQuery = true)
    List<String> getPostsForTheYear(@Param("query") String year);
}