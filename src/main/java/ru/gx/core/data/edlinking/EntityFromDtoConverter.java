package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.data.entity.EntityObject;

import java.util.Collection;

@SuppressWarnings("unused")
public interface EntityFromDtoConverter<DEST extends EntityObject, SOURCE extends DataObject> {

    /**
     * Поиск объекта одного типа по указанному источнику (DataObject).
     * @param source        Объект (DataObject), из которого берем данные.
     */
    @Nullable
    DEST findEntityBySource(@Nullable final SOURCE source);

    /**
     * Создание объекта по источнику (DataObject).
     * @param source        Объект (DataObject), из которого берем данные.
     */
    @NotNull
    DEST createEntityBySource(@NotNull final SOURCE source);

    /**
     * @param destination Объект-назначения данных.
     * @return Допустимо ли изменение объекта-назначения.
     */
    boolean isDestinationUpdatable(@NotNull final DEST destination);

    /**
     * Наполнение destination (EntityObject) данными из source (DataObject).
     * @param destination   Объект, в который загружаем данные.
     * @param source        Объект, из которого берем данные.
     */
    void updateDtoBySource(@NotNull final DEST destination, @NotNull final SOURCE source) throws NotAllowedObjectUpdateException;

    /**
     * Наполнение списка результирующих объектов (EntityObject) из списка объектов-источников (DataObject).
     * @param destination   Список результирующих объектов (EntityObject).
     * @param source        Источник - список объектов-источников (DataObject).
     */
    void fillDtoCollectionFromSource(@NotNull final Collection<DEST> destination, @NotNull final Iterable<SOURCE> source) throws NotAllowedObjectUpdateException;
}
