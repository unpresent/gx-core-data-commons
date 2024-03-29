package ru.gx.core.data.save;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEvent;
import ru.gx.core.channels.AbstractOutcomeChannelHandlerDescriptor;
import ru.gx.core.channels.ChannelApiDescriptor;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.errors.BufferIsFullException;
import ru.gx.core.data.sqlwrapping.SqlCommandWrapper;
import ru.gx.core.messaging.Message;
import ru.gx.core.messaging.MessageBody;
import ru.gx.core.messaging.MessageSimpleBody;
import ru.gx.core.messaging.MessagesFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
@Slf4j
public class DbSavingDescriptor extends AbstractOutcomeChannelHandlerDescriptor {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    @Getter
    @Nullable
    private final Class<? extends Message<? extends MessageBody>> messageClass;

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
    private DbSavingProcessMode processMode = DbSavingDescriptorsDefaults.DEFAULT_PROCESS_MODE;

    /**
     * Способ представления данных при сериализации (отправке данных в БД)
     * <ul>
     *     <li>{@link DbSavingSerializeMode#Json}</li>
     *     <li>{@link DbSavingSerializeMode#Binary}</li>
     * </ul>
     */
    @Getter
    @NotNull
    private DbSavingSerializeMode serializeMode = DbSavingDescriptorsDefaults.DEFAULT_SERIALIZE_MODE;

    /**
     * Режим накопления и отправки данных в БД
     * <ul>
     *      <li>{@link DbSavingAccumulateMode#PerMessage}</li>
     *      <li>{@link DbSavingAccumulateMode#PerObject}</li>
     *      <li>{@link DbSavingAccumulateMode#PerRawObject}</li>
     *      <li>{@link DbSavingAccumulateMode#PerPackage}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfMessages}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfObjects}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfRawObjects}</li>
     *      <li>{@link DbSavingAccumulateMode#ListOfPackages}</li>
     * </ul>
     */
    @Getter
    @NotNull
    private DbSavingAccumulateMode accumulateMode = DbSavingDescriptorsDefaults.DEFAULT_ACCUMULATE_MODE;

    /**
     * Максимальный размер буфера, по достижении которого данные будут сохранены в БД.
     */
    @Getter
    private int bufferLimit = DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_LIMIT;

    @Getter
    private int allowableBufferOversize = (int) (DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_LIMIT * 0.1) + 1;

    /**
     * Максимальное время (в мс), в течение которого требуется копить данные, после этого данные будут сохранены в БД.
     */
    @Getter
    @Setter
    private int bufferForMs = DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_FOR_MS;

    /**
     * Пауза перед повтором попытки сохранения после ошибки по умолчанию.
     */
    @Getter
    @Setter
    private int retryAfterErrorForMs = DbSavingDescriptorsDefaults.DEFAULTS_RETRY_AFTER_ERROR_FOR_MS;

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
    private final List<Message<? extends MessageBody>> messages = new ArrayList<>();

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
     * Буфер RAW-данных
     */
    @Getter(PROTECTED)
    private final List<Object> rawObjects = new ArrayList<>();

    /**
     * Момент последнего сохранения в БД. =System.currentTimeMillis() сразу после сохранения.
     */
    @Getter(PROTECTED)
    private long lastSavedTimeMillis;

    /**
     * Момент последней ошибки при сохранении в БД. =System.currentTimeMillis() сразу после сохранения с ошибкой.
     */
    @Getter(PROTECTED)
    private Long lastErrorTimeMillis;

    @Getter(PROTECTED)
    @Nullable
    private SqlCommandWrapper saveStatement;

    /**
     * Событие, которые вызывается сразу после сохранения данных в БД.
     * При этом оно будет вызвано внутри транзакции, если установлено свойство {@link DbSavingDescriptor#useTransactionDueSave}
     */
    @Getter(PROTECTED)
    @Nullable
    private ApplicationEvent eventAfterSave;

    /**
     * Признак необходимости сохранять данные в транзакции. Если true, то транзакция открывается до начала сохранения,
     * а закрывается после обработки события о сохранении данных.
     */
    @Getter(PROTECTED)
    private boolean useTransactionDueSave = DbSavingDescriptorsDefaults.DEFAULT_USE_TRAN_IN_SAVE;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialize">
    public DbSavingDescriptor(
            @NotNull final AbstractDbSavingConfiguration owner,
            @NotNull final ChannelApiDescriptor<? extends Message<? extends MessageBody>> api,
            @Nullable final DbSavingDescriptorsDefaults defaults
    ) {
        super(owner, api, defaults);
        this.messageClass = api.getMessageClass();
        internalInitDefaults(defaults);
    }

    public DbSavingDescriptor(
            @NotNull final AbstractDbSavingConfiguration owner,
            @NotNull final String channelName,
            @Nullable final DbSavingDescriptorsDefaults defaults
    ) {
        super(owner, channelName, defaults);
        this.messageClass = null;
        internalInitDefaults(defaults);
    }

    private void internalInitDefaults(@Nullable final DbSavingDescriptorsDefaults defaults) {
        if (defaults != null) {
            this.processMode = defaults.getProcessMode();
            this.accumulateMode = defaults.getAccumulateMode();
            this.serializeMode = defaults.getSerializeMode();
            this.bufferLimit = defaults.getBufferLimit();
            this.bufferForMs = defaults.getBufferForMs();
            this.saveOperator = defaults.getSaveOperator();
            this.useTransactionDueSave = defaults.isUseTransactionDueSave();
        }
    }

    /**
     * Настройка Descriptor-а должна заканчиваться этим методом.
     *
     * @return this.
     */
    @Override
    @NotNull
    public DbSavingDescriptor init() throws InvalidParameterException {
        final var descriptorName = getChannelName();

        if (this.saveCommand == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " doesn't have saveCommand!");
        }

        if (this.saveOperator == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " doesn't have operator!");
        }

        if (this.dataObjectClass == null
                && (
                getAccumulateMode() == DbSavingAccumulateMode.PerObject
                        || getAccumulateMode() == DbSavingAccumulateMode.ListOfObjects)
        ) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName +
                    " doesn't have dataObjectClass (for accumulateMode = " + getAccumulateMode() + ")!");
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
    public DbSavingDescriptor unInit() {
        super.unInit();
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional getters & setters">
    @NotNull
    public DbSavingDescriptor setBufferLimit(final int value) {
        this.bufferLimit = value;
        this.allowableBufferOversize = (int) (DbSavingDescriptorsDefaults.DEFAULTS_BUFFER_LIMIT * 0.1) + 1;
        return this;
    }

    @Override
    @NotNull
    public AbstractDbSavingConfiguration getOwner() {
        return (AbstractDbSavingConfiguration) super.getOwner();
    }

    protected void checkMutable(@NotNull final String propertyName) {
        final var descriptorName = getChannelName();
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
    public DbSavingDescriptor setDataObjectClass(@NotNull final Class<? extends DataObject> dataObjectClass) {
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
    public DbSavingDescriptor setDataPackageClass(
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
    public DbSavingDescriptor setMessagesFactory(
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
    public DbSavingDescriptor setSaveOperator(@NotNull final DbSavingOperator saveOperator) {
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
    public DbSavingDescriptor setSaveCommand(@NotNull final String saveCommand) {
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
    public DbSavingDescriptor setProcessMode(@NotNull final DbSavingProcessMode processMode) {
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
    public DbSavingDescriptor setSerializeMode(DbSavingSerializeMode serializeMode) {
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
    public DbSavingDescriptor setAccumulateMode(DbSavingAccumulateMode accumulateMode) {
        if (this.accumulateMode == accumulateMode) {
            return this;
        }
        checkMutable("accumulateMode");
        this.accumulateMode = accumulateMode;
        return this;
    }

    /**
     * Setter свойства useTransactionDueSave
     *
     * @param useTransactionDueSave признак необходимости сохранять данные в транзакции
     * @return this
     */
    @NotNull
    public DbSavingDescriptor setUseTransactionDueSave(boolean useTransactionDueSave) {
        if (this.useTransactionDueSave == useTransactionDueSave) {
            return this;
        }
        checkMutable("useTransactionDueSave");
        this.useTransactionDueSave = useTransactionDueSave;
        return this;
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Saving realization">
    public boolean bufferIsEmpty() {
        return getBuffer().isEmpty();
    }

    protected List<?> getBuffer() {
        return switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> getMessages();
            case PerObject, ListOfObjects -> getObjects();
            case PerRawObject, ListOfRawObjects -> getRawObjects();
            case PerPackage, ListOfPackages -> getPackages();
        };
    }

    /**
     * @return или накоплен буфер достаточного размера, или прошло достаточно времени накопления.
     */
    public boolean readyForSave() {
        if (getProcessMode() == DbSavingProcessMode.Immediate) {
            return true;
        }

        final var buffer = getBuffer();
        if (buffer.isEmpty() || getBufferForMs() <= 0) {
            return false;
        }
        if (getLastSavedIntervalMs() < getBufferForMs() && buffer.size() < getBufferLimit()) {
            return false;
        }

        final var lastErrorIntervalMs = getLastErrorIntervalMs();
        if (lastErrorIntervalMs >= 0) {
            if (lastErrorIntervalMs < getRetryAfterErrorForMs()) {
                return false;
            }
            log.info("Retrying save after error!");
        }

        return true;
    }

    /**
     * @return Сколько миллисекунд прошло последнего сохранения в БД данных (= System.currentTimeMillis() - lastSavedTimeMillis).
     */
    public long getLastSavedIntervalMs() {
        return System.currentTimeMillis() - this.lastSavedTimeMillis;
    }

    /**
     * @return Сколько миллисекунд прошло последнего сохранения в БД данных (= System.currentTimeMillis() - lastSavedTimeMillis).
     */
    public long getLastErrorIntervalMs() {
        return this.lastErrorTimeMillis == null ?
                -1 :
                System.currentTimeMillis() - this.lastErrorTimeMillis;
    }

    /**
     * Добавить в буфер/сохранить немедленно Сообщение
     *
     * @param message Сообщение
     */
    @SuppressWarnings("unchecked")
    public void processMessage(
            @NotNull final Message<? extends MessageBody> message,
            @Nullable final ApplicationEvent eventAfterSave
    ) throws SQLException, IOException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> {
                if (getMessages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getMessages().add(message);
            }
            case PerObject, ListOfObjects -> {
                if (getObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                final var data = internalExtractData(message);
                if (data instanceof final DataObject dataObject) {
                    getObjects().add(dataObject);
                } else if (data instanceof final DataPackage<?> dataPackage) {
                    getObjects().addAll(dataPackage.getObjects());
                } else {
                    throw new UnsupportedOperationException("Unsupported accumulateMode "
                            + getAccumulateMode()
                            + " for body data " + data.getClass().getName());
                }
            }
            case PerRawObject, ListOfRawObjects -> {
                if (getRawObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                final var data = internalExtractData(message);
                if (data instanceof final DataObject dataObject) {
                    getRawObjects().add(dataObject);
                } else if (data instanceof final DataPackage<?> dataPackage) {
                    dataPackage.getObjects().forEach(object -> getObjects().add(object));
                } else if (data instanceof final Collection<?> collection) {
                    getRawObjects().addAll(collection);
                } else {
                    getRawObjects().add(data);
                }
            }
            case PerPackage, ListOfPackages -> {
                if (getPackages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
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
        this.eventAfterSave = eventAfterSave;
        checkNeedToSave();
    }

    protected Object internalExtractData(@NotNull Message<? extends MessageBody> message) {
        final var body = message.getBody();
        if (!(body instanceof final MessageSimpleBody simpleBody)) {
            throw new UnsupportedOperationException("Unsupported accumulateMode "
                    + getAccumulateMode()
                    + " for channel " + getChannelName());
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
     * @param dataObject     объект данных
     * @param eventAfterSave событие, которое будет вызываться через Spring Events после сохранения данных.
     */
    @SuppressWarnings("unchecked")
    public void processObject(
            @NotNull final DataObject dataObject,
            @Nullable final ApplicationEvent eventAfterSave
    ) throws SQLException, IOException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> {
                if (getMessages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                internalCreateAndAddMessageByDataObject(dataObject);
            }
            case PerObject, ListOfObjects -> {
                if (getObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getObjects().add(dataObject);
            }
            case PerRawObject, ListOfRawObjects -> {
                if (getRawObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getRawObjects().add(dataObject);
            }
            case PerPackage, ListOfPackages -> {
                if (getPackages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                final var packs = getPackages();
                final var lastPackage = packs.isEmpty()
                        ? internalCreateAndAddDataPackage()
                        : packs.get(packs.size() - 1);
                final var objects = (Collection<DataObject>) lastPackage.getObjects();
                objects.add(dataObject);
            }
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        this.eventAfterSave = eventAfterSave;
        checkNeedToSave();
    }

    /**
     * Добавить в буфер/сохранить немедленно объект данных
     *
     * @param rawObject      объект RAW-данных
     * @param eventAfterSave событие, которое будет вызываться через Spring Events после сохранения данных.
     */
    public void processRawObject(
            @NotNull final Object rawObject,
            @Nullable final ApplicationEvent eventAfterSave
    ) throws SQLException, IOException {
        switch (getAccumulateMode()) {
            case PerRawObject, ListOfRawObjects -> {
                if (getRawObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getRawObjects().add(rawObject);
            }
            default -> throw new UnsupportedOperationException("Unsupported accumulateMode " + getAccumulateMode());
        }
        this.eventAfterSave = eventAfterSave;
        checkNeedToSave();
    }

    /**
     * Добавить в буфер/сохранить немедленно пакет объектов
     *
     * @param dataPackage пакет объектов данных
     */
    public void processPackage(
            @NotNull final DataPackage<?> dataPackage,
            @Nullable final ApplicationEvent eventAfterSave
    ) throws SQLException, IOException {
        switch (getAccumulateMode()) {
            case PerMessage, ListOfMessages -> {
                if (getMessages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                internalCreateAndAddMessageByDataPackage(dataPackage);
            }
            case PerObject, ListOfObjects -> {
                if (getObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getObjects().addAll(dataPackage.getObjects());
            }
            case PerRawObject, ListOfRawObjects -> {
                if (getRawObjects().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getRawObjects().addAll(dataPackage.getObjects());
            }
            case PerPackage, ListOfPackages -> {
                if (getPackages().isEmpty()) {
                    resetBuffer();
                } else {
                    checkBufferIsFull();
                }
                getPackages().add(dataPackage);
            }
            default -> throw new UnsupportedOperationException("Unknown accumulateMode " + getAccumulateMode());
        }
        this.eventAfterSave = eventAfterSave;
        checkNeedToSave();
    }

    public synchronized void checkNeedToSave() throws SQLException, IOException {
        if (isInitialized() && readyForSave()) {
            internalSaveData();
        }
    }

    protected synchronized void checkBufferIsFull() {
        final var buffer = getBuffer();
        final var bufferSize = buffer.size();
        if (bufferSize <= getBufferLimit()) {
            return;
        }
        if (bufferSize > getBufferLimit() + getAllowableBufferOversize()) {
            throw new BufferIsFullException(
                    "DbSavingDescriptor (" + getChannelName()
                            + ") Buffer is full (size = " + bufferSize + "; limit = " + getBufferLimit() + ")");
        }
    }

    public void resetBuffer() {
        getMessages().clear();
        getObjects().clear();
        getRawObjects().clear();
        getPackages().clear();
        this.lastSavedTimeMillis = System.currentTimeMillis();
    }

    @SneakyThrows({InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    protected void internalCreateAndAddMessageByDataObject(@NotNull final DataObject dataObject) {
        final var descriptorName = getChannelName();
        if (!isInitialized()
                || getMessagesFactory() == null || getMessageType() == null || getMessageVersion() == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " is not initialized!");
        }

        final var result = getMessagesFactory()
                .createByDataObject(
                        null,   // parentId
                        getMessageType(),
                        getMessageVersion(),
                        dataObject,
                        null);
        getMessages().add(result);
    }

    @SneakyThrows({InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    protected void internalCreateAndAddMessageByDataPackage(
            @NotNull final DataPackage<? extends DataObject> dataPackage
    ) {
        final var descriptorName = getChannelName();
        if (!isInitialized()
                || getMessagesFactory() == null || getMessageType() == null || getMessageVersion() == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " is not initialized!");
        }

        final var result = getMessagesFactory()
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
        final var descriptorName = getChannelName();
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
            throws SQLException, IOException {
        final var descriptorName = getChannelName();
        if (!isInitialized()) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " is not initialized!");
        }
        if (getSaveCommand() == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " is not configured (does not defined saveCommand)!");
        }
        if (getSaveOperator() == null) {
            throw new ChannelConfigurationException("Descriptor " + descriptorName + " is not configured (does not defined saveOperator)!");
        }

        // Если пришли сюда процедуру из таймеров разных Descriptor-ов, то
        // физическое само сохранение выполняем через блокировку одного общего ресурса,
        // чтобы не использовать несколько коннектов пула.
        synchronized (getOwner()) {
            final var vSaveOperator = getSaveOperator();
            final var accumulateMode = getAccumulateMode();
            final var started = System.currentTimeMillis();

            try (final var connect = getOwner().getThreadConnectionsWrapper().getCurrentThreadConnection()) {
                if (getSaveStatement() == null || !getSaveStatement().getConnection().isEqual(connect)) {
                    if (getSaveStatement() != null) {
                        final var connection = getSaveStatement().getConnection();
                        if (connection != null) {
                            connection.close();
                        }
                    }
                    this.saveStatement = vSaveOperator.prepareStatement(getSaveCommand(), accumulateMode);
                }

                if (isUseTransactionDueSave()) {
                    connect.openTransaction();
                }
                try {
                    switch (accumulateMode) {
                        case PerMessage, ListOfMessages -> vSaveOperator.saveData(getSaveStatement(), getMessages(), accumulateMode);
                        case PerObject, ListOfObjects -> vSaveOperator.saveData(getSaveStatement(), getObjects(), accumulateMode);
                        case PerRawObject, ListOfRawObjects -> vSaveOperator.saveData(getSaveStatement(), getRawObjects(), accumulateMode);
                        case PerPackage, ListOfPackages -> vSaveOperator.saveData(getSaveStatement(), getPackages(), accumulateMode);
                        default -> throw new IllegalStateException("Unexpected value: " + accumulateMode);
                    }

                    final var event = getEventAfterSave();
                    if (event != null) {
                        getOwner().getEventPublisher().publishEvent(event);
                    }
                    if (isUseTransactionDueSave()) {
                        connect.commitTransaction();
                    }

                    recordMessagesExecuted(
                            getOwner().getConfigurationName(),
                            System.currentTimeMillis() - started,
                            getBuffer().size()
                    );
                    resetBuffer();
                } catch (Exception e) {
                    this.lastErrorTimeMillis = System.currentTimeMillis();
                    log.info(String.format("Error has been caught! Set lastErrorTimeMillis = %d", this.lastErrorTimeMillis));
                    log.error("", e);
                    if (isUseTransactionDueSave()) {
                        connect.rollbackTransaction();
                        throw e;
                    }
                }
            }
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
