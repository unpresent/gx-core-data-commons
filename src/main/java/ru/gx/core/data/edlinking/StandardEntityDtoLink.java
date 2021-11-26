package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.entity.EntitiesPackage;
import ru.gx.core.data.entity.EntityObject;

public class StandardEntityDtoLink<E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
        extends AbstractEntityDtoLink<E, EP, ID, O, P> {
    public StandardEntityDtoLink(@NotNull Class<E> entityClass, @NotNull Class<O> dtoClass) {
        super(entityClass, dtoClass);
    }
}
