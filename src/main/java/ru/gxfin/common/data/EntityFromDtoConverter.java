package ru.gxfin.common.data;

@SuppressWarnings("unused")
public interface EntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, DTO extends DataObject> {
    /**
     * Наполнение EntityObject данными из DTO (DataObject).
     * @param destination   EntityObject.
     * @param source        DTO (DataObject).
     */
    void fillEntityFromDto(DEST destination, DTO source);

    /**
     * Наполнение пакета EntityObjects из пакета DTOs.
     * @param destination   Пакет EntityObjects.
     * @param source        Пакет DTOs.
     */
    void fillEntitiesPackageFromDtoPackage(DESTPACK destination, Iterable<DTO> source);
}
