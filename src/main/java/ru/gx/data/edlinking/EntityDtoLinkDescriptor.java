package ru.gx.data.edlinking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.CrudRepository;
import ru.gx.data.DataMemoryRepository;
import ru.gx.data.DataObject;
import ru.gx.data.DataPackage;
import ru.gx.data.entity.EntitiesPackage;
import ru.gx.data.entity.EntityObject;

@SuppressWarnings("unused")
public interface EntityDtoLinkDescriptor<E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>> {
    @NotNull
    Class<E> getEntityClass();

    @Nullable
    Class<EP> getEntitiesPackageClass();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setEntitiesPackageClass(@Nullable final Class<EP> entitiesPackageClass);

    @NotNull
    Class<O> getDtoClass();

    @Nullable
    Class<P> getDtoPackageClass();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setDtoPackageClass(@Nullable final Class<P> dtoPackageClass);

    @Nullable
    CrudRepository<E, ID> getRepository();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setRepository(@NotNull final CrudRepository<E, ID> repository);

    @Nullable
    DataMemoryRepository<O, P> getMemoryRepository();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setMemoryRepository(@NotNull final DataMemoryRepository<O, P> memoryRepository);

    @Nullable
    EntityFromDtoConverter<E, EP, O> getEntityFromDtoConverter();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setEntityFromDtoConverter(@NotNull final EntityFromDtoConverter<E, EP, O> entityFromDtoConverter);

    @Nullable
    DtoFromEntityConverter<O, P, E> getDtoFromEntityConverter();

    @NotNull
    EntityDtoLinkDescriptor<E, EP, ID, O, P> setDtoFromEntityConverter(@NotNull final DtoFromEntityConverter<O, P, E> dtoFromEntityConverter);

    @NotNull
    P createDtoPackage() throws EntitiesDtoLinksConfigurationException;

    @NotNull
    EP createEntitiesPackage() throws EntitiesDtoLinksConfigurationException;
}
