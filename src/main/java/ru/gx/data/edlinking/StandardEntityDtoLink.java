package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.data.entity.EntitiesPackage;
import ru.gx.data.entity.EntityObject;

public class StandardEntityDtoLink<E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
        extends AbstractEntityDtoLink<E, EP, ID, O, P> {
    public StandardEntityDtoLink(@NotNull Class<E> entityClass, @NotNull Class<O> dtoClass) {
        super(entityClass, dtoClass);
    }
}
