package main.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Интрефейс PostCommentRepository. Слой для работы с БД и сущностью PostComment
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    /**
     * Метод findIdByTime
     * Возвращает Id комментария по времени его создания
     *
     * @param time время создания комментария
     */
    @Query(value = "select id from post_comments where time = :query",
            nativeQuery = true)
    int findIdByTime(@Param("query") Date time);
}
