package ru.gxfin.common.data;

@SuppressWarnings("unused")
public abstract class AbstractEntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, SOURCE extends DataObject>
        implements EntityFromDtoConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillEntityFromDto(DEST destination, SOURCE source);

    @Override
    public void fillEntitiesPackageFromDtoPackage(DESTPACK destination, Iterable<SOURCE> source) {
        final var destObjects = destination.getObjects();
        for (var dto : source) {
            final var entity = getOrCreateEntityByDto(dto);
            fillEntityFromDto(entity, dto);
            destObjects.add(entity);
        }
    }

    protected abstract DEST getOrCreateEntityByDto(SOURCE source);
}
