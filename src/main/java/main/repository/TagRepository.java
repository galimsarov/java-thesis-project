package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
