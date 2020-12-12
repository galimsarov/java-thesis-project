package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GlobalSettingsRepository
        extends JpaRepository<GlobalSetting, Integer> {
    /**
     * Метод multiUser
     * Возвращает соответствуюшее значение из таблицы global_settings
     */
    @Query(value = "select value from global_settings where code = " +
            "'MULTIUSER_MODE'", nativeQuery = true)
    String multiUser();

    /**
     * Метод statisticsIsPublic
     * Возвращает соответствуюшее значение из таблицы global_settings
     */
    @Query(value = "select value from global_settings where code = " +
            "'STATISTICS_IS_PUBLIC'", nativeQuery = true)
    String statisticsIsPublic();
}
