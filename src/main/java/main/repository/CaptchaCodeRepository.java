package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Интрефейс CaptchaCodeRepository. Слой для работы с БД и сущностью CaptchaCode
 *
 * @version 1.0
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Integer> {
    /**
     * Метод findByCode
     * Возвращает экземпляр CaptchaCode по коду
     *
     * @param code код восстановления пароля
     */
    CaptchaCode findByCode(String code);
}
