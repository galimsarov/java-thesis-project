package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Интрефейс UserRepository. Слой для работы с БД и сущностью User
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Метод isAdmin
     * Проверяет является ли пользователь админом / модератором
     *
     * @param name имя пользователя
     */
    @Query(value = "select is_moderator from users where name = :query",
            nativeQuery = true)
    int isAdmin(@Param("query") String name);

    /**
     * Метод findByName
     * Возвращает экземпляр User по имени
     *
     * @param name имя пользователя
     */
    User findByName(String name);

    /**
     * Метод findByEmail
     * Возвращает экземпляр User по email
     *
     * @param email пользователя
     */
    User findByEmail(String email);

    /**
     * Метод findByCode
     * Возвращает экземпляр User по code
     *
     * @param code код восстановления пароля
     */
    User findByCode(String code);
}
