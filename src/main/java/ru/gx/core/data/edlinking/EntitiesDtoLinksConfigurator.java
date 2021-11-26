package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;

/**
 * Реализатор данного интерфейса будет вызван после настройки всех бинов.
 * Задача реализатора данного интерфейса заключается в определении связок Entity и Dto.
 */
@SuppressWarnings("unused")
public interface EntitiesDtoLinksConfigurator {
    /**
     * Вызывается после настройки бинов (по событию ApplicationReadyEvent).
     * @param configuration Передается бин, реализующий интерфейс EntitiesDtosLinksConfiguration. Данный бин в методе реализации требуется настроить.
     */
    void configureLinks(@NotNull final EntitiesDtosLinksConfiguration configuration);
}
