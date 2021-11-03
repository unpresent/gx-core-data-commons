package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.data.InvalidDataObjectTypeException;
import ru.gx.data.entity.EntityObject;

@SuppressWarnings("unused")
public interface DtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject> {
    /**
     * Наполнение DTO (DataObject) данными из EntityObject.
     * @param destination   DTO (DataObject).
     * @param source        EntityObject.
     */
    void fillDtoFromEntity(@NotNull final DEST destination, @NotNull final SOURCE source) throws InvalidDataObjectTypeException;

    /**
     * Наполнение пакета DTOs из пакета EntityObjects.
     * @param destination   Пакет DTOs.
     * @param source        Пакет EntityObjects.
     */
    @SuppressWarnings("rawtypes")
    void fillDtoPackageFromEntitiesPackage(@NotNull final DataPackage destination, @NotNull final Iterable<SOURCE> source) throws InvalidDataObjectTypeException;
}
