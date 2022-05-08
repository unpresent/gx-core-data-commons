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
    private final List<DbSavingDescriptor> descriptors = new ArrayList<>();

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Registration descriptors">
    @NotNull
    public DbSavingDescriptor newDescriptor() {
        final var descriptor = createDescriptor(getSaveOperator());
        getDescriptors().add(descriptor);
        return descriptor;
    }

    @NotNull
    protected abstract DbSavingDescriptor createDescriptor(@NotNull final DbSavingOperator saveOperator);
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Реализация ">
    @Scheduled(initialDelay = DbSavingDescriptor.INTERVAL_MS_FOR_CHECK_NEED_SAVE)
    protected void internalCheckNeedSave()
            throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        for(final var descriptor: getDescriptors()) {
            descriptor.checkNeedToSave();
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
