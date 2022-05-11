package ru.gx.core.data.save;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.gx.core.channels.OutcomeChannelDescriptorsDefaults;

import java.util.Properties;

@SuppressWarnings("unused")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class DbSavingDescriptorsDefaults extends OutcomeChannelDescriptorsDefaults {
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">

    /**
     * Размер буфера (количество объектов, до которого производится накопление перед сохранением) по умолчанию.
     */
    public static final int DEFAULTS_BUFFER_LIMIT = 1000;

    /**
     * Время накопления (в течение которого данные накапливаются в буфере перед сохранением) по умолчанию.
     */
    public static final int DEFAULTS_BUFFER_FOR_MS = 500;

    /**
     * Режим обработки данных (допускается или нет буферизация) по умолчанию.
     */
    public static final DbSavingProcessMode DEFAULT_PROCESS_MODE = DbSavingProcessMode.UseBuffer;

    /**
     * Способ представления данных при сериализации (отправке данных в БД) по умолчанию.
     */
    public static final DbSavingSerializeMode DEFAULT_SERIALIZE_MODE = DbSavingSerializeMode.Json;

    /**
     * Режим накопления и отправки данных в БД.
     */
    public static final DbSavingAccumulateMode DEFAULT_ACCUMULATE_MODE = DbSavingAccumulateMode.ListOfObjects;

    public static final boolean DEFAULT_USE_TRAN_IN_SAVE = true;
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Режим обработки данных (допускается или нет буферизация).
     */
    @NotNull
    private DbSavingProcessMode processMode = DEFAULT_PROCESS_MODE;

    /**
     * Способ представления данных при сериализации (отправке данных в БД).
     */
    @NotNull
    private DbSavingSerializeMode serializeMode = DEFAULT_SERIALIZE_MODE;

    /**
     * Режим накопления и отправки данных в БД.
     */
    @NotNull
    private DbSavingAccumulateMode accumulateMode = DEFAULT_ACCUMULATE_MODE;

    /**
     * Размер буфера (количество объектов, до которого производится накопление перед сохранением).
     */
    private int bufferLimit = DEFAULTS_BUFFER_LIMIT;

    /**
     * Время накопления (в течение которого данные накапливаются в буфере перед сохранением).
     */
    private int bufferForMs = DEFAULTS_BUFFER_FOR_MS;

    /**
     * Реализация метода сохранения в БД
     */
    @Nullable
    private DbSavingOperator saveOperator;

    private boolean useTransactionDueSave;
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
