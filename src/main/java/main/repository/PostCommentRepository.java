package main.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Интрефейс PostCommentRepository. Слой для работы с БД и сущностью PostComment
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface PostCommentRepository extends
        JpaRepository<PostComment, Integer> {
    /**
     * Метод findIdByText
     * Возвращает Id комментария по его тексту
     *
     * @param text текст комментария
     */
    @Query(value = "SELECT id FROM post_comments where text = :query",
            nativeQuery = true)
    int findIdByText(@Param("query") String text);
}
