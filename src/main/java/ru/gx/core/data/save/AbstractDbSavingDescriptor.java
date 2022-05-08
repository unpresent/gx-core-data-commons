package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
@Accessors(chain = true)
public abstract class AbstractDbSavingDescriptor
        implements DbSavingDescriptor, DbSavingBufferResetAbleDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    /**
     * Класс объектов сохраняемых данных
     */
    @Getter
    @Nullable
    private Class<? extends DataObject> dataObjectClass;

    /**
     * Класс пакетов объектов сохраняемых данных
     */
    @Getter
    @Nullable
    private Class<? extends DataPackage<? extends DataObject>> dataPackageClass;

    /**
     * Реализация метода сохранения в БД
     */
    @Getter(PROTECTED)
    @NotNull
    private final DbSavingOperator saveOperator;

    /**
     * SQL сохранения в БД
     */
    @Getter
    @Nullable
    private String saveCommand;

    /**
     * Режим обработки данных (допускается или нет буферизация)
     * <ul>
     *     <li>{@link DbSavingProcessMode#Immediate}</li>
     *     <li>{@link DbSavingProcessMode#UseBuffer}</li>
     * </ul>
     */
    @Getter
    @NotNull
    private DbSavingProcessMode processMode;

    /**
     * Способ представления данных при сериализации (отправке данных в БД)
     * <ul>
     *     <li>{@link DbSavingSerializeMode#Json}</li>
     *     <li>{@link DbSavingSerializeMode#Binary}</li>
     * </ul>
     */
    @Getter
    @NotNull
    private DbSavingSerializeMode serializeMode;

    /**
     * Режим накопления и отправки данных в БД
     * <ul>
     *      <li>{@link DbSavingAccumulateMode#PerObject}</li>
     *      <li>{@link DbSavingAccumulateMode#PerPackage}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfObjects}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfPackages}</li>
     * </ul>
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
     * Момент последнего сохранения в БД данных =System.currentTimeMillis() сразу после сохранения.
     */
    long lastSavedTimeMillis;

    /**
     * Буфер объектов
     */
    @Getter(AccessLevel.PROTECTED)
    private final List<DataObject> objects = new ArrayList<>();

    /**
     * Буфер пакетов объектов
     */
    @Getter(AccessLevel.PROTECTED)
    private final List<DataPackage<? extends DataObject>> packages = new ArrayList<>();

    @Getter(PROTECTED)
    @Nullable
    private Object saveStatement;

    /**
     * Инициализировал ли описатель. После инициализации нельзя менять ряд параметров.
     */
    @Getter
    private boolean initialized;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    protected AbstractDbSavingDescriptor(
            @NotNull final DbSavingOperator saveOperator
    ) {
        super();
        this.processMode = DEFAULT_PROCESS_MODE;
        this.serializeMode = DEFAULT_SERIALIZE_MODE;
        this.accumulateMode = DEFAULT_ACCUMULATE_MODE;
        this.bufferLimit = DEFAULTS_BUFFER_LIMIT;
        this.bufferForMs = DEFAULTS_BUFFER_FOR_MS;

        this.saveOperator = saveOperator;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements DbSavingDescriptor">
    protected void checkMutable(@NotNull final String propertyName) {
        final var descriptorName = getDataObjectClass() != null
                ? getDataObjectClass().getName()
                : getDataPackageClass() != null
                ? getDataPackageClass().getName()
                : "unknown";
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " can't change property " + propertyName + " after initialization!");
        }
    }

    /**
     * Setter свойства saveCommand
     *
     * @param saveCommand SQL сохранения данных
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor setSaveCommand(@NotNull final String saveCommand) {
        if (saveCommand.equals(this.saveCommand)) {
            return this;
        }
        checkMutable("saveCommand");
        this.saveCommand = saveCommand;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingProcessMode} processMode
     *
     * @param processMode режим обработки данных
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor setProcessMode(@NotNull final DbSavingProcessMode processMode) {
        if (this.processMode == processMode) {
            return this;
        }
        checkMutable("processMode");
        this.processMode = processMode;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingSerializeMode} serializeMode
     *
     * @param serializeMode режим сериализации данных:
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor setSerializeMode(DbSavingSerializeMode serializeMode) {
        if (this.serializeMode == serializeMode) {
            return this;
        }
        checkMutable("serializeMode");
        this.serializeMode = serializeMode;
        return this;
    }

    /**
     * Setter свойства {@link DbSavingAccumulateMode} accumulateMode
     *
     * @param accumulateMode вариант накопления данных:
     * @return this
     */
    @NotNull
    public AbstractDbSavingDescriptor setAccumulateMode(DbSavingAccumulateMode accumulateMode) {
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

    /**
     * @return или накоплен буфер достаточного размера, или прошло достаточно времени накопления.
     */
    @Override
    public boolean readyForSave() {
        return getProcessMode() == DbSavingProcessMode.Immediate
                || getObjects().size() >= getBufferLimit()
                || getLastSavedIntervalMs() >= getBufferForMs();
    }

    /**
     * @return Сколько миллисекунд прошло последнего сохранения в БД данных (= System.currentTimeMillis() - lastSavedTimeMillis).
     */
    @Override
    public long getLastSavedIntervalMs() {
        return System.currentTimeMillis() - this.lastSavedTimeMillis;
    }

    /**
     * Добавить в буфер/сохранить немедленно объект данных
     *
     * @param dataObject объект данных
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addSavingObject(@NotNull DataObject dataObject)
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (getAccumulateMode()) {
            case PerObject, ListOfObjects -> getObjects().add(dataObject);
            case PerPackage, ListOfPackages -> {
                final var packs = getPackages();
                final var lastPackage = packs.isEmpty()
                        ? internalCreateAndAddDataPackage()
                        : packs.get(packs.size() - 1);
                final var objects = (Collection<DataObject>)lastPackage.getObjects();
                objects.add(dataObject);
            }
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        checkNeedToSave();
    }

    /**
     * Добавить в буфер/сохранить немедленно пакет объектов
     *
     * @param dataPackage пакет объектов данных
     */
    @Override
    public void addSavingPackage(@NotNull DataPackage<?> dataPackage)
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (getAccumulateMode()) {
            case PerObject, ListOfObjects -> dataPackage
                    .getObjects()
                    .forEach(o -> getObjects().add(o));
            case PerPackage, ListOfPackages -> getPackages()
                    .add(dataPackage);
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        checkNeedToSave();
    }

    @Override
    public void init() {
        final var descriptorName = getDataObjectClass() != null
                ? getDataObjectClass().getName()
                : getDataPackageClass() != null
                ? getDataPackageClass().getName()
                : "unknown";
        if (getDataObjectClass() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not configured (does not defined dataObjectClass)!");
        }
        if (getDataPackageClass() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not configured (does not defined dataPackageClass)!");
        }
        if (getSaveCommand() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not configured (does not defined saveCommand)!");
        }
        this.initialized = true;
    }

    public void checkNeedToSave() throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        if (isInitialized() && readyForSave()) {
            internalSaveData();
        }
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
    // <editor-fold desc="Обработка механизмов сохранения">
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @NotNull
    protected DataPackage<DataObject> internalCreateAndAddDataPackage() {
        final var descriptorName = getDataObjectClass() != null
                ? getDataObjectClass().getName()
                : getDataPackageClass() != null
                ? getDataPackageClass().getName()
                : "unknown";
        if (!isInitialized() || getDataPackageClass() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of "
                    + descriptorName
                    + " is not initialized!");
        }
        final var constructor = getDataPackageClass().getConstructor();
        final var result = (DataPackage<DataObject>)constructor.newInstance();
        getPackages().add(result);
        return result;
    }

    protected synchronized void internalSaveData()
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        final var descriptorName = getDataObjectClass() != null
                ? getDataObjectClass().getName()
                : getDataPackageClass() != null
                ? getDataPackageClass().getName()
                : "unknown";
        if (getDataObjectClass() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not initialized!");
        }
        if (!isInitialized()) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not initialized!");
        }
        if (getSaveCommand() == null) {
            throw new ChannelConfigurationException("Descriptor for saving data objects of " + descriptorName + " is not configured (does not defined saveCommand)!");
        }

        try {
            final var vSaveOperator = getSaveOperator();
            final var accumulateMode = getAccumulateMode();
            if (getSaveStatement() == null) {
                this.saveStatement = vSaveOperator.prepareStatement(getSaveCommand(), accumulateMode);
            }
            switch (accumulateMode) {
                case PerObject, ListOfObjects -> vSaveOperator.saveData(getSaveStatement(), getObjects(), accumulateMode);
                case PerPackage, ListOfPackages -> vSaveOperator.saveData(getSaveStatement(), getPackages(), accumulateMode);
                default -> throw new IllegalStateException("Unexpected value: " + accumulateMode);
            }
        } finally {
            resetBuffer();
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
