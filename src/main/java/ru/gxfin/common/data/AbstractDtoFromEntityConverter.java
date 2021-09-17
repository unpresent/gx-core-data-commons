package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject>
        implements DtoFromEntityConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillDtoFromEntity(@NotNull DEST destination, @NotNull SOURCE source);

    @Override
    public void fillDtoPackageFromEntitiesPackage(@NotNull DESTPACK destination, Iterable<SOURCE> source) {
        if (source == null) {
            return;
        }
        final var destObjects = destination.getObjects();
        for (var entity : source) {
            final var dto = getOrCreateDtoByEntity(entity);
            fillDtoFromEntity(dto, entity);
            destObjects.add(dto);
        }
    }

    protected abstract DEST getOrCreateDtoByEntity(@NotNull SOURCE source);
}
