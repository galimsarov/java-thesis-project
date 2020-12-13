package main.repository;

import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {
    /**
     * Метод getPostVoteByPostAndUser
     * Метод получения лайка или дизлайка
     *
     * @param postId
     * @param userId
     */
    @Query(value = "select * from post_votes where post_id = :postQuery and " +
            "user_id = :userQuery", nativeQuery = true)
    PostVote getPostVoteByPostAndUser(@Param("postQuery") int postId,
                                      @Param("userQuery") int userId);
}
