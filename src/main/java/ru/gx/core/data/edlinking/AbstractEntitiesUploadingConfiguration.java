package ru.gx.core.data.edlinking;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.channels.ChannelApiDescriptor;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.entity.EntityObject;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEntitiesUploadingConfiguration implements EntitiesUploadingConfiguration {
    @NotNull
    private final ArrayList<
            EntityUploadingDescriptor<
                    ? extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
                    ? extends EntityObject, ? extends DataObject>> descriptorsList = new ArrayList<>();

    @SuppressWarnings("rawtypes")
    @NotNull
    private final Map<ChannelApiDescriptor,
            EntityUploadingDescriptor<
                    ? extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
                    ? extends EntityObject, ? extends DataObject>> descriptorsByChannels = new HashMap<>();

    protected AbstractEntitiesUploadingConfiguration() {
        super();
    }

    @Override
    public int size() {
        return this.descriptorsList.size();
    }

    @Override
    @NotNull
    public Iterable<EntityUploadingDescriptor<
            ? extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            ? extends EntityObject, ? extends DataObject>> getAll() {
        return this.descriptorsList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public <CH extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            E extends EntityObject, O extends DataObject>
    EntityUploadingDescriptor<CH, E, O> getByChannel(
            @NotNull final CH channelApiDescriptor
    ) throws EntitiesDtoLinksConfigurationException {
        final var result = this.descriptorsByChannels.get(channelApiDescriptor);
        if (result == null) {
            throw new EntitiesDtoLinksConfigurationException("Can't get descriptor by channel " + channelApiDescriptor.getClass().getName());
        }
        return (EntityUploadingDescriptor<CH, E, O>) result;
    }


    @Override
    @NotNull
    public <CH extends ChannelApiDescriptor<? extends Message<? extends MessageBody>>,
            E extends EntityObject, O extends DataObject>
    EntityUploadingDescriptor<CH, E, O> newDescriptor(
            @NotNull final CH channelApiDescriptor,
            @NotNull final Class<E> entityClass,
            @NotNull final Class<O> dataObjectClass
    ) throws EntitiesDtoLinksConfigurationException {
        if (this.descriptorsByChannels.containsKey(channelApiDescriptor)) {
            throw new EntitiesDtoLinksConfigurationException("channelApiDescriptor " + channelApiDescriptor.getClass().getName() + " already registered!");
        }
        final var result = new StandardEntityUploadingDescriptor<>(channelApiDescriptor, entityClass, dataObjectClass);
        this.descriptorsList.add(result);
        this.descriptorsByChannels.put(channelApiDescriptor, result);
        return result;
    }
}
