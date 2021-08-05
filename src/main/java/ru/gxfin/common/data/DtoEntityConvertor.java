package ru.gxfin.common.data;

@SuppressWarnings("unused")
public interface DtoEntityConvertor<DTO extends DataObject, DTOPACK extends DataPackage<DTO>, ENTITY extends EntityObject, ENTITYPACK extends EntitiesPackage<ENTITY>> {
    /**
     * Наполнение DTO (DataObject) данными из EntityObject.
     * @param destination   DTO (DataObject).
     * @param source        EntityObject.
     */
    void setDtoFromEntity(DTO destination, ENTITY source);

    /**
     * Наполнение EntityObject данными из DTO (DataObject).
     * @param destination   EntityObject.
     * @param source        DTO (DataObject).
     */
    void setEntityFromDto(ENTITY destination, DTO source);

    /**
     * Наполнение пакета DTOs из пакета EntityObjects.
     * @param destination   Пакет DTOs.
     * @param source        Пакет EntityObjects.
     */
    void setDtoPackageFromEntitiesPackage(DTOPACK destination, Iterable<ENTITY> source) throws ObjectCreateException;

    /**
     * Наполнение пакета EntityObjects из пакета DTOs.
     * @param destination   Пакет EntityObjects.
     * @param source        Пакет DTOs.
     */
    void setEntitiesPackageFromDtoPackage(ENTITYPACK destination, Iterable<DTO> source) throws ObjectCreateException;
}
