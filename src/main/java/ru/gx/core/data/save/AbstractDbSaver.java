package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractDbSaver {
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Constants">

    /**
     * Интервалы времени, через которые (1/частота) производится проверка на необходимость сохранения данных
     * (период накопления не может быть меньше данной величины)
     */
    public static final int INTERVAL_MS_FOR_CHECK_NEED_SAVE = 10;
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    /**
     * Реализация метода сохранения в БД
     */
    @Getter(PROTECTED)
    @NotNull
    private final DbSavingOperator saveOperator;

    /**
     * Список описателей
     */
    @Getter(PROTECTED)
    @NotNull
    private final List<AbstractDbSavingConfiguration> configurations = new ArrayList<>();

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Реализация ">
    @Scheduled(initialDelay = INTERVAL_MS_FOR_CHECK_NEED_SAVE)
    protected void internalCheckNeedSave()
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        for (final var config : getConfigurations()) {
            for (final var descriptor : config.getAll()) {
                final var savingDescriptor = (DbSavingDescriptor<?>)descriptor;
                savingDescriptor.checkNeedToSave();
            }
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
