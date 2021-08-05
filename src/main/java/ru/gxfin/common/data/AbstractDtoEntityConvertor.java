package ru.gxfin.common.data;

@SuppressWarnings("unused")
public abstract class AbstractDtoEntityConvertor<DTO extends DataObject, DTOPACK extends DataPackage<DTO>, ENTITY extends EntityObject, ENTITYPACK extends EntitiesPackage<ENTITY>>
        implements DtoEntityConvertor<DTO, DTOPACK, ENTITY, ENTITYPACK> {
    @Override
    public abstract void setDtoFromEntity(DTO destination, ENTITY source);

    @Override
    public abstract void setEntityFromDto(ENTITY destination, DTO source);

    @Override
    public void setDtoPackageFromEntitiesPackage(DTOPACK destination, Iterable<ENTITY> source) throws ObjectCreateException {
        final var destObjects = destination.getObjects();
        for (var entity : source) {
            final var dto = getOrCreateDtoByEntity(entity);
            setDtoFromEntity(dto, entity);
            destObjects.add(dto);
        }
    }

    @Override
    public void setEntitiesPackageFromDtoPackage(ENTITYPACK destination, Iterable<DTO> source) throws ObjectCreateException {
        final var destObjects = destination.getObjects();
        for (var dto : source) {
            final var entity = getOrCreateEntityByDto(dto);
            setEntityFromDto(entity, dto);
            destObjects.add(entity);
        }
    }

    protected abstract DTO getOrCreateDtoByEntity(ENTITY entity) throws ObjectCreateException;

    protected abstract ENTITY getOrCreateEntityByDto(DTO entity) throws ObjectCreateException;
}
