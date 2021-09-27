package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface EntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, DTO extends DataObject> {
    /**
     * Наполнение EntityObject данными из DTO (DataObject).
     * @param destination   EntityObject.
     * @param source        DTO (DataObject).
     */
    void fillEntityFromDto(@NotNull DEST destination, @NotNull DTO source) throws InvalidDataObjectTypeException;

    /**
     * Наполнение пакета EntityObjects из пакета DTOs.
     * @param destination   Пакет EntityObjects.
     * @param source        Пакет DTOs.
     */
    void fillEntitiesPackageFromDtoPackage(@NotNull DESTPACK destination, Iterable<DTO> source) throws InvalidDataObjectTypeException;
}
