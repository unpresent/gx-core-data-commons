package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface DtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject> {
    /**
     * Наполнение DTO (DataObject) данными из EntityObject.
     * @param destination   DTO (DataObject).
     * @param source        EntityObject.
     */
    void fillDtoFromEntity(@NotNull DEST destination, @NotNull SOURCE source) throws InvalidDataObjectTypeException;

    /**
     * Наполнение пакета DTOs из пакета EntityObjects.
     * @param destination   Пакет DTOs.
     * @param source        Пакет EntityObjects.
     */
    void fillDtoPackageFromEntitiesPackage(@NotNull DESTPACK destination, Iterable<SOURCE> source) throws InvalidDataObjectTypeException;
}
