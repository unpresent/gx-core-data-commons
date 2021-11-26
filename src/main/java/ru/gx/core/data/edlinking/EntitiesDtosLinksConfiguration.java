package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.entity.EntitiesPackage;
import ru.gx.core.data.entity.EntityObject;

public interface EntitiesDtosLinksConfiguration {
    int size();

    @NotNull
    Iterable<EntityDtoLinkDescriptor<?, ?, ?, ?, ?>> getAll();

    @NotNull
    <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> getByEntityClass(@NotNull final Class<E> entityClass) throws EntitiesDtoLinksConfigurationException;

    @NotNull
    <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> getByDtoClass(@NotNull final Class<O> dtoClass) throws EntitiesDtoLinksConfigurationException;

    @NotNull
    <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> newDescriptor(
            @NotNull final Class<E> entityClass,
            @NotNull final Class<O> dtoClass
    );

}
