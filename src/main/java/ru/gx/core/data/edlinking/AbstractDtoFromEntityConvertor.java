package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.core.data.entity.EntityObject;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromEntityConvertor<DEST extends DataObject, SOURCE extends EntityObject>
        implements DtoFromEntityConvertor<DEST, SOURCE> {

    /**
     * Поиск объекта одного типа по указанному источнику другого типа.
     *
     * @param source Объект (EntityObject), из которого берем данные.
     */
    @Override
    @Nullable
    public abstract DEST findDtoBySource(@Nullable SOURCE source);

    /**
     * Создание объекта по источнику.
     *
     * @param source Объект (EntityObject), из которого берем данные.
     */
    @Override
    @NotNull
    public abstract DEST createDtoBySource(@NotNull SOURCE source);

    /**
     * @param destination Объект-назначения данных.
     * @return Допустимо ли изменение объекта-назначения.
     */
    @Override
    public abstract boolean isDestinationUpdatable(@NotNull DEST destination);

    /**
     * Наполнение destination (DataObject) данными из source (EntityObject).
     *
     * @param destination Объект, в который загружаем данные.
     * @param source      Объект, из которого берем данные.
     */
    @Override
    public abstract void updateDtoBySource(@NotNull DEST destination, @NotNull SOURCE source) throws NotAllowedObjectUpdateException;

    /**
     * Наполнение списка результирующих объектов (DataObject) из списка объектов-источников (EntityObject).
     *
     * @param destination Список результирующих объектов (DataObject).
     * @param source      Источник - список объектов-источников (EntityObject).
     */
    @Override
    public void fillDtoCollectionFromSource(@NotNull Collection<DEST> destination, @NotNull Iterable<SOURCE> source) throws NotAllowedObjectUpdateException {
        for (var sourceObject : source) {
            var destObject = findDtoBySource(sourceObject);
            if (destObject == null) {
                destObject = createDtoBySource(sourceObject);
            } else if (isDestinationUpdatable(destObject)) {
                updateDtoBySource(destObject, sourceObject);
            }
            destination.add(destObject);
        }
    }
}
