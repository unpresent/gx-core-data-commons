package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.InvalidDataObjectTypeException;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.data.entity.EntitiesPackage;
import ru.gx.data.entity.EntityObject;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class AbstractEntityFromDtoConverter<DEST extends EntityObject, SOURCE extends DataObject>
        implements EntityFromDtoConverter<DEST, SOURCE> {

    /**
     * Поиск объекта одного типа по указанному источнику (DataObject).
     * @param source        Объект (DataObject), из которого берем данные.
     */
    @Override
    public abstract DEST findDtoBySource(@NotNull SOURCE source);

    /**
     * Создание объекта по источнику (DataObject).
     * @param source        Объект (DataObject), из которого берем данные.
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
     * Наполнение destination (EntityObject) данными из source (DataObject).
     * @param destination   Объект, в который загружаем данные.
     * @param source        Объект, из которого берем данные.
     */
    @Override
    public abstract void updateDtoBySource(@NotNull DEST destination, @NotNull SOURCE source) throws NotAllowedObjectUpdateException;

    /**
     * Наполнение списка результирующих объектов (EntityObject) из списка объектов-источников (DataObject).
     * @param destination   Список результирующих объектов (EntityObject).
     * @param source        Источник - список объектов-источников (DataObject).
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
