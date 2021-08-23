package ru.gxfin.common.data;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject>
        implements DtoFromEntityConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillDtoFromEntity(DEST destination, SOURCE source);

    @Override
    public void fillDtoPackageFromEntitiesPackage(DESTPACK destination, Iterable<SOURCE> source) {
        final var destObjects = destination.getObjects();
        for (var entity : source) {
            final var dto = getOrCreateDtoByEntity(entity);
            fillDtoFromEntity(dto, entity);
            destObjects.add(dto);
        }
    }

    protected abstract DEST getOrCreateDtoByEntity(SOURCE source);
}
