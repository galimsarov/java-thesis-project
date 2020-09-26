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
 * Interface PostRepository. Data layer for Post entity
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
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

    @Query(value = "SELECT * FROM posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time() " +
            "order by time",
            nativeQuery = true)
    Page<Post> findEarlyPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time() " +
            "order by time desc",
            nativeQuery = true)
    Page<Post> findRecentPosts(Pageable pageable);

    @Query(value = "SELECT * FROM posts where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and (posts.text like concat('%',:query,'%') " +
            "or posts.title like concat('%',:query,'%'))",
            nativeQuery = true)
    List<Post> searchForPostsByQuery
            (@Param("query") String query, Pageable pageable);

    @Query(value = "select * from posts where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time > :dateBefore " +
            " and time < :dateAfter and time < current_time()",
            nativeQuery = true)
    List<Post> getPostsByDate(@Param("dateBefore") String dateBefore,
                              @Param("dateAfter") String dateAfter,
                              Pageable pageable);

    @Query(value = "select * from posts join (select tag2post.id, " +
            "tag2post.post_id, tags.name as tag_name from tag2post " +
            "join tags on tag2post.tag_id = tags.id) as temp on " +
            "posts.id = temp.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED' and posts.time < " +
            "current_time() and temp.tag_name = :query",
            nativeQuery = true)
    List<Post> getPostsByTag(@Param("query") String tag, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'NEW'",
            nativeQuery = true)
    List<Post> getNewPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'DECLINED'",
            nativeQuery = true)
    List<Post> getDeclinedPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as admins on posts.moderator_id = " +
            "admins.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED'",
            nativeQuery = true)
    List<Post> getAcceptedPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 0",
            nativeQuery = true)
    List<Post> getMyInactivePosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'NEW'",
            nativeQuery = true)
    List<Post> getMyPendingPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'DECLINED'",
            nativeQuery = true)
    List<Post> getMyDeclinedPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts join (select id, name from users " +
            "where name = :query) as temp_users on posts.user_id = " +
            "temp_users.id where posts.is_active = 1 and " +
            "posts.moderation_status = 'ACCEPTED'",
            nativeQuery = true)
    List<Post> getMyPublishedPosts
            (@Param("query") String name, Pageable pageable);

    @Query(value = "select * from posts where id = :query and " +
            "is_active = 1 and moderation_status = 'ACCEPTED' and " +
            "time <= current_time()", nativeQuery = true)
    Post getPost(@Param("query") int id);
}