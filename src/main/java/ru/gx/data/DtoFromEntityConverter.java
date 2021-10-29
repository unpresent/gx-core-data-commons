package ru.gx.data;

import org.jetbrains.annotations.NotNull;

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
    void fillDtoPackageFromEntitiesPackage(@NotNull final DESTPACK destination, @NotNull final Iterable<SOURCE> source) throws InvalidDataObjectTypeException;
}
