package ru.gxfin.common.data;

@SuppressWarnings("unused")
public interface DtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject> {
    /**
     * Наполнение DTO (DataObject) данными из EntityObject.
     * @param destination   DTO (DataObject).
     * @param source        EntityObject.
     */
    void fillDtoFromEntity(DEST destination, SOURCE source);

    /**
     * Наполнение пакета DTOs из пакета EntityObjects.
     * @param destination   Пакет DTOs.
     * @param source        Пакет EntityObjects.
     */
    void fillDtoPackageFromEntitiesPackage(DESTPACK destination, Iterable<SOURCE> source);

}
