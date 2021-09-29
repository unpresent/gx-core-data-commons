package ru.gx.data.jpa;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.InvalidDataObjectTypeException;

@SuppressWarnings("unused")
public abstract class AbstractEntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, SOURCE extends DataObject>
        implements EntityFromDtoConverter<DEST, DESTPACK, SOURCE> {
    @Override
    public abstract void fillEntityFromDto(@NotNull final DEST destination, @NotNull final SOURCE source) throws InvalidDataObjectTypeException;

    @Override
    public void fillEntitiesPackageFromDtoPackage(@NotNull final DESTPACK destination, @NotNull final Iterable<SOURCE> source) throws InvalidDataObjectTypeException {
        final var destObjects = destination.getObjects();
        for (var dto : source) {
            final var entity = getOrCreateEntityByDto(dto);
            fillEntityFromDto(entity, dto);
            destObjects.add(entity);
        }
    }

    @NotNull
    protected abstract DEST getOrCreateEntityByDto(@NotNull final SOURCE source) throws InvalidDataObjectTypeException;
}
