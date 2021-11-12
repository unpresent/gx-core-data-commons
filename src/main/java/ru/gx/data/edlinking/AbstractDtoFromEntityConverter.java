package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.data.InvalidDataObjectTypeException;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.data.entity.EntityObject;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromEntityConverter<DEST extends DataObject, SOURCE extends EntityObject>
        implements DtoFromEntityConverter<DEST, SOURCE> {

    /**
     * Поиск объекта одного типа по указанному источнику другого типа.
     *
     * @param source Объект (EntityObject), из которого берем данные.
     */
    @Override
    public abstract DEST findDtoBySource(@NotNull SOURCE source);

    /**
     * Создание объекта по источнику.
     *
     * @param source Объект (EntityObject), из которого берем данные.
     */
    @Override
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
