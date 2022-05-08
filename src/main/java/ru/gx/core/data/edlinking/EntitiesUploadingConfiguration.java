package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.channels.ChannelApiDescriptor;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.entity.EntityObject;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

public interface EntitiesUploadingConfiguration {
    int size();

    @NotNull
    Iterable<EntityUploadingDescriptor<
            ? extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            ? extends EntityObject, ? extends DataObject>> getAll();

    @NotNull
    <CH extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            E extends EntityObject, O extends DataObject>
    EntityUploadingDescriptor<CH, E, O> getByChannel(
            @NotNull final CH channelApiDescriptor
    ) throws EntitiesDtoLinksConfigurationException;

    @NotNull
    <CH extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            E extends EntityObject, O extends DataObject>
    EntityUploadingDescriptor<CH, E, O> newDescriptor(
            @NotNull final CH channelApiDescriptor,
            @NotNull final Class<E> entityClass,
            @NotNull final Class<O> dataObjectClass
    ) throws EntitiesDtoLinksConfigurationException;

}
