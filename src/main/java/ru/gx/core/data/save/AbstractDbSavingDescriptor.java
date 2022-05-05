package ru.gx.core.data.save;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Accessors(chain = true)
public abstract class AbstractDbSavingDescriptor<O extends DataObject, P extends DataPackage<O>>
        implements DbSavingDescriptor<O>, DbSavingBufferResetAbleDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    /**
     * Класс объектов сохраняемых данных
     */
    @Getter
    private final Class<O> dataObjectClass;

    /**
     * Класс пакетов объектов сохраняемых данных
     */
    @Getter
    private final Class<P> dataPackageClass;

    /**
     * SQL сохранения в БД
     */
    @Getter
    @Nullable
    private String saveCommand;

    /**
     * Режим обработки данных (допускается или нет буферизация)
     */
    @Getter
    @NotNull
    private DbSavingProcessMode processMode;

    /**
     * Способ представления данных при сериализации (отправке данных в БД)
     */
    @Getter
    @NotNull
    private DbSavingSerializeMode serializeMode;

    /**
     * Режим накопления и отправки данных в БД. При значении == None свойства BufferLimit и BufferForMs не имеют смысла
     */
    @Getter
    @NotNull
    private DbSavingAccumulateMode accumulateMode;

    /**
     * Максимальный размер буфера, по достижении которого данные будут сохранены в БД.
     */
    @Getter
    @Setter
    int bufferLimit;

    /**
     * Максимальное время (в мс), в течение которого требуется копить данные, после этого данные будут сохранены в БД.
     */
    @Getter
    @Setter
    int bufferForMs;

    /**
     * Момент последнего сохранения в БД данных (= System.currentTimeMillis() сразу после сохранения.
     */
    long lastSavedTimeMillis;

    /**
     * Буфер объектов
     */
    @Getter(AccessLevel.PROTECTED)
    private final List<O> objects = new ArrayList<>();

    /**
     * Буфер пакетов объектов
     */
    @Getter(AccessLevel.PROTECTED)
    private final List<P> packages = new ArrayList<>();

    /**
     * Инициализировал ли описатель. После инициализации нельзя менять ряд параметров.
     */
    @Getter
    private boolean initialized;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    @SuppressWarnings("unchecked")
    protected AbstractDbSavingDescriptor(
    ) {
        super();
        this.processMode = DEFAULT_PROCESS_MODE;
        this.serializeMode = DEFAULT_SERIALIZE_MODE;
        this.accumulateMode = DEFAULT_ACCUMULATE_MODE;
        this.bufferLimit = DEFAULTS_BUFFER_LIMIT;
        this.bufferForMs = DEFAULTS_BUFFER_FOR_MS;

        final var thisClass = this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.dataObjectClass = (Class<O>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
            this.dataPackageClass = (Class<P>) ((ParameterizedType) superClass).getActualTypeArguments()[1];
        } else {
            throw new InvalidParameterException("Unable to determine data objects class!");
        }
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements DbSavingDescriptor">
    protected void checkMutable(@NotNull final String propertyName) {
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + getDataObjectClass().getName() + " can't change property " + propertyName + " after initialization!");
        }
    }

    /**
     * Setter свойства saveCommand
     *
     * @param saveCommand SQL сохранения данных
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor<O, P> setSaveCommand(@NotNull final String saveCommand) {
        if (saveCommand.equals(this.saveCommand)) {
            return this;
        }
        checkMutable("saveCommand");
        this.saveCommand = saveCommand;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingProcessMode} processMode
     * <ul>
     *     <li>{@link DbSavingProcessMode#Immediate}</li>
     *     <li>{@link DbSavingProcessMode#UseBuffer}</li>
     * </ul>
     *
     * @param processMode режим обработки данных
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor<O, P> setProcessMode(@NotNull final DbSavingProcessMode processMode) {
        if (this.processMode == processMode) {
            return this;
        }
        checkMutable("processMode");
        this.processMode = processMode;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingSerializeMode} serializeMode
     * <ul>
     *     <li>{@link DbSavingSerializeMode#Json}</li>
     *     <li>{@link DbSavingSerializeMode#Binary}</li>
     * </ul>
     *
     * @param serializeMode режим сериализации данных:
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor<O, P> setSerializeMode(DbSavingSerializeMode serializeMode) {
        if (this.serializeMode == serializeMode) {
            return this;
        }
        checkMutable("serializeMode");
        this.serializeMode = serializeMode;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingAccumulateMode} accumulateMode
     * <ul>
     *      <li>{@link DbSavingAccumulateMode#PerObject}</li>
     *      <li>{@link DbSavingAccumulateMode#PerPackage}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfObjects}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfPackages}</li>
     * </ul>
     *
     * @param accumulateMode вариант накопления данных:
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor<O, P> setAccumulateMode(DbSavingAccumulateMode accumulateMode) {
        if (this.accumulateMode == accumulateMode) {
            return this;
        }
        checkMutable("accumulateMode");
        this.accumulateMode = accumulateMode;
        return this;
    }

    @Override
    public boolean bufferIsEmpty() {
        return getObjects().isEmpty();
    }

    @Override
    public boolean readyForSave() {
        return getProcessMode() == DbSavingProcessMode.Immediate
                || getObjects().size() >= getBufferLimit()
                || getLastSavedIntervalMs() >= getBufferForMs();
    }

    @Override
    public long getLastSavedIntervalMs() {
        return System.currentTimeMillis() - this.lastSavedTimeMillis;
    }

    @Override
    public void init() {
        if (getSaveCommand() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + getDataObjectClass().getName() + " is not configured (does not defined saveCommand)!");
        }
        this.initialized = true;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements DbSavingBufferResetAbleDescriptor">
    @Override
    public void resetBuffer() {
        this.objects.clear();
        this.lastSavedTimeMillis = System.currentTimeMillis();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
