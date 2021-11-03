package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.InvalidDataObjectTypeException;
import ru.gx.data.entity.EntitiesPackage;
import ru.gx.data.entity.EntityObject;

@SuppressWarnings("unused")
public interface EntityFromDtoConverter<DEST extends EntityObject, DESTPACK extends EntitiesPackage<DEST>, DTO extends DataObject> {
    /**
     * Наполнение EntityObject данными из DTO (DataObject).
     * @param destination   EntityObject.
     * @param source        DTO (DataObject).
     */
    void fillEntityFromDto(@NotNull final DEST destination, @NotNull final DTO source) throws InvalidDataObjectTypeException;

    /**
     * Наполнение пакета EntityObjects из пакета DTOs.
     * @param destination   Пакет EntityObjects.
     * @param source        Пакет DTOs.
     */
    @SuppressWarnings("rawtypes")
    void fillEntitiesPackageFromDtoPackage(@NotNull final EntitiesPackage destination, @NotNull final Iterable<DTO> source) throws InvalidDataObjectTypeException;
}
