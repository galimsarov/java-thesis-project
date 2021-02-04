package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Интрефейс TagRepository. Слой для работы с БД и сущностью Tag
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */

public interface TagRepository extends JpaRepository<Tag, Integer> {
    /**
     * Метод findNamesOfTags
     * Возвращает списик имён тэгов
     */
    @Query(value = "SELECT name FROM tags", nativeQuery = true)
    List<String> findNamesOfTags();

    /**
     * Метод findTagByName
     * Возвращает тэг по имени
     * @param name имя тэга
     */
    @Query(value = "SELECT * FROM tags where name=:query", nativeQuery = true)
    Tag findTagByName(@Param("query") String name);

    /**
     * Метод findByName
     * Возвращает имена тэгов, начинающихся с указанной строки
     */
    @Query(value = "select name from tags where name like concat(:query,'%')",
            nativeQuery = true)
    List<String> findByName(@Param("query") String query);
}
