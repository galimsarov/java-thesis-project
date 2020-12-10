package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Интрефейс CaptchaCodeRepository. Слой для работы с БД и сущностью CaptchaCode
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    /**
     * Метод deleteOldCaptchas
     * Удаляет устаревшие капчи из таблицы
     *
     * @param time время устаревания
     */
    @Modifying
    @Transactional
    @Query(value = "delete from captcha_codes where time < " +
            "subdate(current_time(), interval :query hour)", nativeQuery = true)
    void deleteOldCaptchas(@Param("query") int time);

    /**
     * Метод findSecretByCode
     * Возвращает секретный код по коду
     *
     * @param code
     */
    @Query(value = "select secret_code from captcha_codes where code = " +
            ":query", nativeQuery = true)
    String findSecretByCode(@Param("query") String code);
}
