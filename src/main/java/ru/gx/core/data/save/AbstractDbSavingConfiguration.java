package ru.gx.core.data.save;

import org.jetbrains.annotations.NotNull;
import ru.gx.core.channels.AbstractChannelDescriptorsDefaults;
import ru.gx.core.channels.AbstractChannelsConfiguration;
import ru.gx.core.channels.ChannelDirection;
import ru.gx.core.channels.ChannelHandlerDescriptor;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;

public abstract class AbstractDbSavingConfiguration extends AbstractChannelsConfiguration {
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractDbSavingConfiguration(@NotNull final String configurationName) {
        super(ChannelDirection.Out, configurationName);
    }

    @Override
    protected AbstractChannelDescriptorsDefaults createChannelDescriptorsDefaults() {
        return new DbSavingDescriptorsDefaults();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Реализация OutcomeTopicsConfiguration">
    @Override
    protected <M extends Message<? extends MessageBody>, D extends ChannelHandlerDescriptor<M>>
    boolean allowCreateDescriptor(@NotNull Class<D> aClass) {
        return false;
    }

    @Override
    public @NotNull DbSavingDescriptorsDefaults getDescriptorsDefaults() {
        return (DbSavingDescriptorsDefaults)super.getDescriptorsDefaults();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
