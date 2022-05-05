package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import ru.gx.core.channels.ChannelConfigurationException;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;
import static ru.gx.core.data.save.DbSavingAccumulateMode.PerObject;
import static ru.gx.core.data.save.DbSavingAccumulateMode.PerPackage;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public abstract class AbstractDbSavingSaver {
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
    private final List<DbSavingDescriptor<?>> descriptors;
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Реализация ">

    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="">
    @RequiredArgsConstructor
    private static class Handler<O extends DataObject, P extends DataPackage<O>> {
        @Getter
        private final AbstractDbSavingSaver owner;

        @Getter
        private final AbstractDbSavingDescriptor<O, P> descriptor;

        @Getter(PROTECTED)
        private final ArrayList<O> objects = new ArrayList<>();

        @Getter(PROTECTED)
        private final ArrayList<P> packages = new ArrayList<>();

        @Getter
        @Nullable
        private Object saveStatement;

        @Scheduled()
        protected final void checkNeedToSave() throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
            if (getDescriptor().getProcessMode() == DbSavingProcessMode.Immediate) {
                return;
            }
            if (getDescriptor().readyForSave()) {
                saveData();
            }
        }

        protected void checkDescriptorIsReady() {
            final var vvDescriptor = getDescriptor();
            if (!vvDescriptor.isInitialized()) {
                throw new ChannelConfigurationException("Descriptor for saving data objects of " + vvDescriptor.getDataObjectClass().getName() + " is not initialized!");
            }
            if (vvDescriptor.getSaveCommand() == null) {
                throw new ChannelConfigurationException("Descriptor for saving data objects of " + vvDescriptor.getDataObjectClass().getName() + " is not configured (does not defined saveCommand)!");
            }
        }

        protected void saveData() throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
            checkDescriptorIsReady();
            final var vDescriptor = getDescriptor();
            if (!vDescriptor.readyForSave()) {
                return;
            }
            try {
                final var vSaveOperator = getOwner().getSaveOperator();
                final var accumulateMode = vDescriptor.getAccumulateMode();
                if (getSaveStatement() == null) {
                    // Проверка на null есть в checkDescriptorIsReady()
                    //noinspection ConstantConditions
                    this.saveStatement = vSaveOperator.prepareStatement(vDescriptor.getSaveCommand(), accumulateMode);
                }
                switch (accumulateMode) {
                    case PerObject, ListOfObjects -> vSaveOperator.saveData(getSaveStatement(), getObjects(), accumulateMode);
                    case PerPackage, ListOfPackages -> vSaveOperator.saveData(getSaveStatement(), getPackages(), accumulateMode);
                    default -> throw new IllegalStateException("Unexpected value: " + accumulateMode);
                }

            } finally {
                getDescriptor().resetBuffer();
            }
        }

    }
    // </editor-fold>
    // -------------------------------------------------------------------------------------------------------------
}
