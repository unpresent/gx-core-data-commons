package ru.gx.core.data.edlinking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
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

@Getter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class StandardEntityUploadingDescriptor
        <CH extends ChannelApiDescriptor<? extends Message<? extends MessageHeader, ? extends MessageBody>>,
                E extends EntityObject, O extends DataObject>
        implements EntityUploadingDescriptor<CH, E, O> {

    @NotNull
    private final CH channelApiDescriptor;

    @NotNull
    private final Class<E> entityClass;

    @Setter
    @Nullable
    private Class<? extends EntitiesPackage<E>> entitiesPackageClass;

    @NotNull
    private final Class<O> dtoClass;

    @Setter
    @Nullable
    private Class<? extends DataPackage<O>> dtoPackageClass;

    @Setter
    @Nullable
    private CrudRepository<E, ?> repository;

    @Setter
    @Nullable
    private DtoFromEntityConverter<O, E> dtoFromEntityConverter;

    @Setter
    @Nullable
    private DataObjectKeyExtractor<O> keyExtractor;

    public StandardEntityUploadingDescriptor(
            @NotNull final CH channelApiDescriptor,
            @NotNull final Class<E> entityClass,
            @NotNull final Class<O> dtoClass
    ) {
        this.channelApiDescriptor = channelApiDescriptor;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }
}
