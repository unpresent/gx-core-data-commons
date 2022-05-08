package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.AbstractOutcomeChannelHandlerDescriptor;
import ru.gx.core.channels.ChannelApiDescriptor;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageSimpleBody;
import ru.gx.core.messaging.MessagesFactory;

import javax.activation.UnsupportedDataTypeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class DbSavingDescriptor<M extends Message<? extends MessageBody>>
        extends AbstractOutcomeChannelHandlerDescriptor<M> {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    @Getter
    @NotNull
    private final Class<M> messageClass;

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
     * Типы сообщений (используется при создании новых сообщений)
     */
    @Getter
    @Nullable
    private String messageType;

    /**
     * Версия соообщений (используется при создании новых сообщений)
     */
    @Getter
    private Integer messageVersion;

    /**
     * Фабрика сообщений
     */
    @Getter
    @Nullable
    private MessagesFactory messagesFactory;

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
     *      <li>{@link DbSavingAccumulateMode#PerMessage}</li>
     *      <li>{@link DbSavingAccumulateMode#PerObject}</li>
     *      <li>{@link DbSavingAccumulateMode#PerPackage}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfMessages}</li>
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
    private int bufferLimit;

    /**
     * Максимальное время (в мс), в течение которого требуется копить данные, после этого данные будут сохранены в БД.
     */
    @Getter
    @Setter
    private int bufferForMs;

    /**
     * Реализация метода сохранения в БД
     */
    @Getter(PROTECTED)
    @Nullable
    private DbSavingOperator saveOperator;

    /**
     * SQL сохранения в БД
     */
    @Getter
    @Nullable
    private String saveCommand;

    /**
     * Буфер сообщений
     */
    @Getter(PROTECTED)
    private final List<M> messages = new ArrayList<>();

    /**
     * Буфер объектов
     */
    @Getter(PROTECTED)
    private final List<DataObject> objects = new ArrayList<>();

    /**
     * Буфер пакетов объектов
     */
    @Getter(PROTECTED)
    private final List<DataPackage<? extends DataObject>> packages = new ArrayList<>();

    /**
     * Момент последнего сохранения в БД данных =System.currentTimeMillis() сразу после сохранения.
     */
    @Getter(PROTECTED)
    private long lastSavedTimeMillis;

    @Getter(PROTECTED)
    @Nullable
    private Object saveStatement;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    @SuppressWarnings("unchecked")
    public DbSavingDescriptor(
            @NotNull final AbstractDbSavingConfiguration owner,
            @NotNull final ChannelApiDescriptor<M> api,
            @Nullable final DbSavingDescriptorsDefaults defaults
    ) {
        super(owner, api, defaults);
        if (defaults != null) {
            this.processMode = defaults.getProcessMode();
            this.accumulateMode = defaults.getAccumulateMode();
            this.serializeMode = defaults.getSerializeMode();
            this.bufferLimit = defaults.getBufferLimit();
            this.bufferForMs = defaults.getBufferForMs();
            this.saveOperator = defaults.getSaveOperator();
        } else {
            this.processMode = DbSavingDescriptorsDefaults.DEFAULT_PROCESS_MODE;
            this.accumulateMode = DbSavingDescriptorsDefaults.DEFAULT_ACCUMULATE_MODE;
            this.serializeMode = DbSavingDescriptorsDefaults.DEFAULT_SERIALIZE_MODE;
            this.bufferLimit = DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_LIMIT;
            this.bufferForMs = DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_FOR_MS;
        }

        final var thisClass = this.getClass();
        final var superClass = thisClass.getGenericSuperclass();
        if (superClass != null) {
            this.messageClass = (Class<M>) ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new ChannelConfigurationException("Descriptor for saving messages doesn't have messageClass!");
        }
    }

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @Override
    @NotNull
    public DbSavingDescriptor<M> init() throws InvalidParameterException {
        final var descriptorName = getMessageClass().getName();

        if (this.saveCommand == null) {
            throw new ChannelConfigurationException("Descriptor for saving messages "
                    + descriptorName + " doesn't have saveCommand!");
        }

        if (this.saveOperator == null) {
            throw new ChannelConfigurationException("Descriptor for saving messages "
                    + descriptorName + " doesn't have operator!");
        }

        if (this.dataObjectClass == null
                && (
                getAccumulateMode() == DbSavingAccumulateMode.PerObject
                        || getAccumulateMode() == DbSavingAccumulateMode.ListOfObjects)
        ) {
            throw new ChannelConfigurationException("Descriptor for saving messages "
                    + descriptorName
                    + " doesn't have dataObjectClass (for accumulateMode = " + getAccumulateMode() + ")!");
        }

        if (this.dataPackageClass == null
                && (
                getAccumulateMode() == DbSavingAccumulateMode.PerPackage
                        || getAccumulateMode() == DbSavingAccumulateMode.ListOfPackages)
        ) {
            throw new ChannelConfigurationException("Descriptor for saving messages "
                    + descriptorName
                    + " doesn't have dataPackageClass (for accumulateMode = " + getAccumulateMode() + ")!");
        }

        if ((this.messagesFactory == null || this.messageType == null || messageVersion == null)
                && (
                getAccumulateMode() == DbSavingAccumulateMode.PerMessage
                        || getAccumulateMode() == DbSavingAccumulateMode.ListOfMessages)
        ) {
            throw new ChannelConfigurationException("Descriptor for saving messages "
                    + descriptorName
                    + " doesn't have messageFactory (for accumulateMode = " + getAccumulateMode() + ")!");
        }

        super.init();
        return this;
    }

    @NotNull
    public DbSavingDescriptor<M> unInit() {
        super.unInit();
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">
    @Override
    @NotNull
    public AbstractDbSavingConfiguration getOwner() {
        return (AbstractDbSavingConfiguration) super.getOwner();
    }

    protected void checkMutable(@NotNull final String propertyName) {
        final var descriptorName = this.messageClass.getName();
        if (isInitialized()) {
            throw new ChannelConfigurationException("Descriptor for saving of " + descriptorName + " can't change property " + propertyName + " after initialization!");
        }
    }

    /**
     * Setter свойства Класс объектов сохраняемых данных
     *
     * @param dataObjectClass класс объектов сохраняемых данных
     * @return this
     */
    @NotNull
    public DbSavingDescriptor<M> setDataObjectClass(@NotNull final Class<? extends DataObject> dataObjectClass) {
        if (dataObjectClass.equals(this.dataObjectClass)) {
            return this;
        }
        checkMutable("dataObjectClass");
        this.dataObjectClass = dataObjectClass;
        return this;
    }

    /**
     * Setter свойства Класс пакетов объектов сохраняемых данных
     *
     * @param dataPackageClass класс пакетов объектов сохраняемых данных
     * @return this
     */
    @NotNull
    public DbSavingDescriptor<M> setDataPackageClass(
            @NotNull final Class<? extends DataPackage<? extends DataObject>> dataPackageClass
    ) {
        if (dataPackageClass.equals(this.dataPackageClass)) {
            return this;
        }
        checkMutable("dataPackageClass");
        this.dataPackageClass = dataPackageClass;
        return this;
    }

    /**
     * Setter свойства Фабрика сообщений
     *
     * @param messagesFactory Фабрика сообщений
     * @return this
     */
    @NotNull
    public DbSavingDescriptor<M> setMessagesFactory(
            @NotNull final MessagesFactory messagesFactory
    ) {
        if (messagesFactory.equals(this.messagesFactory)) {
            return this;
        }
        checkMutable("messagesFactory");
        this.messagesFactory = messagesFactory;
        return this;
    }

    /**
     * Setter свойства saveOperator
     *
     * @param saveOperator Реализация метода сохранения в БД
     * @return this
     */
    @NotNull
    public DbSavingDescriptor<M> setSaveOperator(@NotNull final DbSavingOperator saveOperator) {
        if (saveOperator.equals(this.saveOperator)) {
            return this;
        }
        checkMutable("saveOperator");
        this.saveOperator = saveOperator;
        return this;
    }

    /**
     * Setter свойства saveCommand
     *
     * @param saveCommand SQL сохранения данных
     * @return this
     */
    @NotNull
    public DbSavingDescriptor<M> setSaveCommand(@NotNull final String saveCommand) {
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
    public DbSavingDescriptor<M> setProcessMode(@NotNull final DbSavingProcessMode processMode) {
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
    public DbSavingDescriptor<M> setSerializeMode(DbSavingSerializeMode serializeMode) {
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
    public DbSavingDescriptor<M> setAccumulateMode(DbSavingAccumulateMode accumulateMode) {
        if (this.accumulateMode == accumulateMode) {
            return this;
        }
        checkMutable("accumulateMode");
        this.accumulateMode = accumulateMode;
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Saving realization">
    public boolean bufferIsEmpty() {
        return getObjects().isEmpty();
    }

    /**
     * @return или накоплен буфер достаточного размера, или прошло достаточно времени накопления.
     */
    public boolean readyForSave() {
        return getProcessMode() == DbSavingProcessMode.Immediate
                || getObjects().size() >= getBufferLimit()
                || getLastSavedIntervalMs() >= getBufferForMs();
    }

    /**
     * @return Сколько миллисекунд прошло последнего сохранения в БД данных (= System.currentTimeMillis() - lastSavedTimeMillis).
     */
    public long getLastSavedIntervalMs() {
        return System.currentTimeMillis() - this.lastSavedTimeMillis;
    }

    /**
     * Добавить в буфер/сохранить немедленно Сообщение
     *
     * @param message Сообщение
     */
    @SuppressWarnings("unchecked")
    public void processMessage(@NotNull final M message)
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> getMessages().add(message);
            case PerObject, ListOfObjects -> {
                final var data = internalExtractData(message);
                if (data instanceof final DataObject dataObject) {
                    getObjects().add(dataObject);
                } else if (data instanceof final DataPackage<?> dataPackage) {
                    getPackages().add(dataPackage);
                } else {
                    throw new UnsupportedOperationException("Unsupported accumulateMode "
                            + getAccumulateMode()
                            + " for body data " + data.getClass().getName());
                }
            }
            case PerPackage, ListOfPackages -> {
                final var packs = getPackages();
                final var lastPackage = packs.isEmpty()
                        ? internalCreateAndAddDataPackage()
                        : packs.get(packs.size() - 1);
                final var data = internalExtractData(message);
                if (data instanceof final DataObject dataObject) {
                    final var objects = (Collection<DataObject>) lastPackage.getObjects();
                    objects.add(dataObject);
                } else if (data instanceof final DataPackage<?> dataPackage) {
                    getPackages().add(dataPackage);
                } else {
                    throw new UnsupportedOperationException("Unsupported accumulateMode "
                            + getAccumulateMode()
                            + " for body data " + data.getClass().getName());
                }

            }
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        checkNeedToSave();
    }

    protected Object internalExtractData(@NotNull M message) {
        final var body = message.getBody();
        if (!(body instanceof final MessageSimpleBody simpleBody)) {
            throw new UnsupportedOperationException("Unsupported accumulateMode "
                    + getAccumulateMode()
                    + " for messages " + getMessageClass().getName());
        }
        final var data = simpleBody.getData();
        if (data == null) {
            throw new NullPointerException("There isn't body in message " + message);
        }
        return data;
    }

    /**
     * Добавить в буфер/сохранить немедленно объект данных
     *
     * @param dataObject объект данных
     */
    @SuppressWarnings("unchecked")
    public void processObject(@NotNull DataObject dataObject)
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> internalCreateAndAddMessageByDataObject(dataObject);
            case PerObject, ListOfObjects -> getObjects().add(dataObject);
            case PerPackage, ListOfPackages -> {
                final var packs = getPackages();
                final var lastPackage = packs.isEmpty()
                        ? internalCreateAndAddDataPackage()
                        : packs.get(packs.size() - 1);
                final var objects = (Collection<DataObject>) lastPackage.getObjects();
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
    public void processPackage(@NotNull DataPackage<?> dataPackage)
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> internalCreateAndAddMessageByDataPackage(dataPackage);
            case PerObject, ListOfObjects -> dataPackage
                    .getObjects()
                    .forEach(o -> getObjects().add(o));
            case PerPackage, ListOfPackages -> getPackages()
                    .add(dataPackage);
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        checkNeedToSave();
    }

    public void checkNeedToSave() throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        if (isInitialized() && readyForSave()) {
            internalSaveData();
        }
    }

    public void resetBuffer() {
        this.objects.clear();
        this.lastSavedTimeMillis = System.currentTimeMillis();
    }

    @SneakyThrows({InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    @SuppressWarnings("unchecked")
    protected void internalCreateAndAddMessageByDataObject(@NotNull final DataObject dataObject) {
        final var descriptorName = getMessageClass().getName();
        if (!isInitialized()
                || getMessagesFactory() == null || getMessageType() == null || getMessageVersion() == null) {
            throw new ChannelConfigurationException("Descriptor for saving of "
                    + descriptorName
                    + " is not initialized!");
        }

        final var result = (M)
                getMessagesFactory()
                        .createByDataObject(
                                null,   // parentId
                                getMessageType(),
                                getMessageVersion(),
                                dataObject,
                                null);
        getMessages().add(result);
    }

    @SneakyThrows({InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    @SuppressWarnings("unchecked")
    protected void internalCreateAndAddMessageByDataPackage(
            @NotNull final DataPackage<? extends DataObject> dataPackage
    ) {
        final var descriptorName = getMessageClass().getName();
        if (!isInitialized()
                || getMessagesFactory() == null || getMessageType() == null || getMessageVersion() == null) {
            throw new ChannelConfigurationException("Descriptor for saving of "
                    + descriptorName
                    + " is not initialized!");
        }

        final var result = (M)
                getMessagesFactory()
                        .createByDataPackage(
                                null,   // parentId
                                getMessageType(),
                                getMessageVersion(),
                                dataPackage,
                                null);
        getMessages().add(result);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @NotNull
    protected DataPackage<DataObject> internalCreateAndAddDataPackage() {
        final var descriptorName = getMessageClass().getName();
        if (!isInitialized() || getDataPackageClass() == null) {
            throw new ChannelConfigurationException("Descriptor for saving of "
                    + descriptorName
                    + " is not initialized!");
        }
        final var constructor = getDataPackageClass().getConstructor();
        final var result = (DataPackage<DataObject>) constructor.newInstance();
        getPackages().add(result);
        return result;
    }

    protected synchronized void internalSaveData()
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        final var descriptorName = getMessageClass().getName();
        if (!isInitialized()) {
            throw new ChannelConfigurationException("Descriptor for saving of " + descriptorName + " is not initialized!");
        }
        if (getSaveCommand() == null) {
            throw new ChannelConfigurationException("Descriptor for saving of " + descriptorName + " is not configured (does not defined saveCommand)!");
        }
        if (getSaveOperator() == null) {
            throw new ChannelConfigurationException("Descriptor for saving of " + descriptorName + " is not configured (does not defined saveOperator)!");
        }

        try {
            final var vSaveOperator = getSaveOperator();
            final var accumulateMode = getAccumulateMode();
            if (getSaveStatement() == null) {
                this.saveStatement = vSaveOperator.prepareStatement(getSaveCommand(), accumulateMode);
            }
            switch (accumulateMode) {
                case PerMessage, ListOfMessages -> vSaveOperator.saveData(getSaveStatement(), getMessages(), accumulateMode);
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