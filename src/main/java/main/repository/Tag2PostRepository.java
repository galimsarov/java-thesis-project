package main.repository;

import main.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интрефейс Tag2PostRepository. Слой для работы с БД и сущностью TagToPost
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface Tag2PostRepository extends JpaRepository<TagToPost, Integer> {
    @Query(value = "select tag2post.id, post_id, tag_id from tag2post join posts " +
            "on tag2post.post_id = posts.id where is_active = 1 and " +
            "moderation_status = 'ACCEPTED' and time < current_time()",
            nativeQuery = true)
    List<TagToPost> findActiveTagToPosts();
}
