package ru.gxfin.common.data;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class AbstractEntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, SOURCE extends DataObject>
        implements EntityFromDtoConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillEntityFromDto(@NotNull DEST destination, @NotNull SOURCE source) throws InvalidDataObjectTypeException;

    @Override
    public void fillEntitiesPackageFromDtoPackage(@NotNull DESTPACK destination, Iterable<SOURCE> source) throws InvalidDataObjectTypeException {
        if (source == null) {
            return;
        }
        final var destObjects = destination.getObjects();
        for (var dto : source) {
            final var entity = getOrCreateEntityByDto(dto);
            fillEntityFromDto(entity, dto);
            destObjects.add(entity);
        }
    }

    protected abstract DEST getOrCreateEntityByDto(SOURCE source) throws InvalidDataObjectTypeException;
}
