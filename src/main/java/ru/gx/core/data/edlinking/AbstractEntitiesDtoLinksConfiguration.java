package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.entity.EntitiesPackage;
import ru.gx.core.data.entity.EntityObject;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEntitiesDtoLinksConfiguration implements EntitiesDtosLinksConfiguration {
    @NotNull
    private final Map<Class<? extends EntityObject>, EntityDtoLinkDescriptor<?, ?, ?, ?, ?>> linksByEntity = new HashMap<>();

    @NotNull
    private final Map<Class<? extends DataObject>, EntityDtoLinkDescriptor<?, ?, ?, ?, ?>> linksByDto = new HashMap<>();

    protected AbstractEntitiesDtoLinksConfiguration() {
        super();
    }

    @Override
    public int size() {
        return this.linksByEntity.size();
    }

    @NotNull
    @Override
    public Iterable<EntityDtoLinkDescriptor<?, ?, ?, ?, ?>> getAll() {
        return this.linksByEntity.values();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> getByEntityClass(@NotNull Class<E> entityClass) throws EntitiesDtoLinksConfigurationException {
        final var result = this.linksByEntity.get(entityClass);
        if (result == null) {
            throw new EntitiesDtoLinksConfigurationException("Can't get descriptor by Dto-Class " + entityClass.getName());
        }
        return (EntityDtoLinkDescriptor<E, EP, ID, O, P>) result;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> getByDtoClass(@NotNull final Class<O> dtoClass) throws EntitiesDtoLinksConfigurationException {
        final var result = this.linksByDto.get(dtoClass);
        if (result == null) {
            throw new EntitiesDtoLinksConfigurationException("Can't get descriptor by Dto-Class " + dtoClass.getName());
        }
        return (EntityDtoLinkDescriptor<E, EP, ID, O, P>) result;
    }

    @Override
    @NotNull
    public <E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
    EntityDtoLinkDescriptor<E, EP, ID, O, P> newDescriptor(
            @NotNull final Class<E> entityClass,
            @NotNull final Class<O> dtoClass
    ) {
        final var result = new StandardEntityDtoLink<E, EP, ID, O, P>(entityClass, dtoClass);
        this.linksByEntity.put(entityClass, result);
        this.linksByDto.put(dtoClass, result);
        return result;
    }
}
