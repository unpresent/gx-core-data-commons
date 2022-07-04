package ru.gx.core.data.save;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import ru.gx.core.channels.AbstractChannelDescriptorsDefaults;
import ru.gx.core.channels.AbstractChannelsConfiguration;
import ru.gx.core.channels.ChannelDirection;
import ru.gx.core.channels.ChannelHandlerDescriptor;
import ru.gx.core.data.sqlwrapping.ThreadConnectionsWrapper;

/**
 * Базовый класс для конфигураций сохранения потоков в БД.
 */
public abstract class AbstractDbSavingConfiguration
        extends AbstractChannelsConfiguration
        implements DbSavingConfiguration {
    @Getter
    @NotNull
    private final ApplicationEventPublisher eventPublisher;

    @Getter
    @NotNull
    private final ThreadConnectionsWrapper threadConnectionsWrapper;

    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractDbSavingConfiguration(
            @NotNull final String configurationName,
            @NotNull final ApplicationEventPublisher eventPublisher,
            @NotNull final ThreadConnectionsWrapper threadConnectionsWrapper
    ) {
        super(ChannelDirection.Out, configurationName);
        this.eventPublisher = eventPublisher;
        this.threadConnectionsWrapper = threadConnectionsWrapper;
    }

    @Override
    protected AbstractChannelDescriptorsDefaults createChannelDescriptorsDefaults() {
        return new DbSavingDescriptorsDefaults();
    }

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Реализация OutcomeTopicsConfiguration">
    @Override
    protected <D extends ChannelHandlerDescriptor>
    boolean allowCreateDescriptor(@NotNull Class<D> descriptorClass) {
        return DbSavingDescriptor.class.isAssignableFrom(descriptorClass);
    }

    @Override
    public @NotNull DbSavingDescriptorsDefaults getDescriptorsDefaults() {
        return (DbSavingDescriptorsDefaults) super.getDescriptorsDefaults();
    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
