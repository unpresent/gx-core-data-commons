package ru.gx.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class AbstractDtoFromEntityConverter<DEST extends DataObject, DESTPACK extends DataPackage<DEST>, SOURCE extends EntityObject>
        implements DtoFromEntityConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillDtoFromEntity(@NotNull final DEST destination, @NotNull final SOURCE source) throws InvalidDataObjectTypeException;

    @Override
    public void fillDtoPackageFromEntitiesPackage(@NotNull final DESTPACK destination, @NotNull final Iterable<SOURCE> source) throws InvalidDataObjectTypeException {
        final var destObjects = destination.getObjects();
        for (var entity : source) {
            final var dto = getOrCreateDtoByEntity(entity);
            fillDtoFromEntity(dto, entity);
            destObjects.add(dto);
        }
    }

    @NotNull
    protected abstract DEST getOrCreateDtoByEntity(@NotNull final SOURCE source) throws InvalidDataObjectTypeException;
}
