package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.CrudRepository;
import ru.gx.core.channels.ChannelApiDescriptor;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataObjectKeyExtractor;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.entity.EntitiesPackage;
import ru.gx.core.data.entity.EntityObject;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

@SuppressWarnings("unused")
public interface EntityUploadingDescriptor
        <CH extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
                E extends EntityObject, O extends DataObject> {

    @NotNull
    CH getChannelApiDescriptor();

    @NotNull
    Class<E> getEntityClass();

    @Nullable
    Class<? extends EntitiesPackage<E>> getEntitiesPackageClass();

    @NotNull
    EntityUploadingDescriptor<CH, E, O> setEntitiesPackageClass(@Nullable final Class<? extends EntitiesPackage<E>> entitiesPackageClass);

    @NotNull
    Class<O> getDtoClass();

    @Nullable
    Class<? extends DataPackage<O>> getDtoPackageClass();

    @NotNull
    EntityUploadingDescriptor<CH, E, O> setDtoPackageClass(@Nullable final Class<? extends DataPackage<O>> dtoPackageClass);

    @Nullable
    CrudRepository<E, ?> getRepository();

    @NotNull
    EntityUploadingDescriptor<CH, E, O> setRepository(@NotNull final CrudRepository<E, ?> repository);

    @Nullable
    DtoFromEntityConverter<O, E> getDtoFromEntityConverter();

    @NotNull
    EntityUploadingDescriptor<CH, E, O> setDtoFromEntityConverter(@NotNull final DtoFromEntityConverter<O, E> dtoFromEntityConverter);

    @Nullable
    DataObjectKeyExtractor<O> getKeyExtractor();

    @NotNull
    EntityUploadingDescriptor<CH, E, O> setKeyExtractor(@Nullable final DataObjectKeyExtractor<O> keyExtractor);
}
