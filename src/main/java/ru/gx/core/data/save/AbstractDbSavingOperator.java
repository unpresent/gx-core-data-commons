package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.sqlwrapping.SqlCommandWrapper;
import ru.gx.core.messaging.Message;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;
import java.util.ArrayList;

@Accessors(chain = true)
public abstract class AbstractDbSavingOperator
        implements DbSavingOperator {

    @Getter(AccessLevel.PROTECTED)
    @NotNull
    private final ObjectMapper objectMapper;

    protected AbstractDbSavingOperator(
            @NotNull final ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }


    /**
     * Подготовка оператора к работе
     *
     * @param sqlCommand     SQL сохранения данных
     * @param accumulateMode режим накопления (объединения) данных перед сохранением
     * @return Statement сохранения
     * @throws SQLException ошибка при работе с БД
     */
    @Override
    @NotNull
    public abstract SqlCommandWrapper prepareStatement(
            @NotNull final String sqlCommand,
            @NotNull final DbSavingAccumulateMode accumulateMode
    ) throws SQLException;

    /**
     * Сохранение данных в БД
     *
     * @param statement      Statement обращения к БД
     * @param data           данные, в зависимости от {@param accumulateMode} допускаются:
     *                       <ul>
     *                            <li>{@link DataObject}</li>
     *                            <li>Iterable of {@link DataObject}</li>
     *                            <li>{@link DataPackage}</li>
     *                            <li>Iterable of {@link DataPackage}</li>
     *                       </ul>
     * @param accumulateMode режим накопления (объединения) данных перед сохранением
     * @throws SQLException ошибка при работе с БД
     */
    @Override
    public void saveData(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data,
            @NotNull final DbSavingAccumulateMode accumulateMode
    ) throws SQLException, UnsupportedDataTypeException, JsonProcessingException {
        switch (accumulateMode) {
            case PerMessage -> internalSavePerMessage(statement, data);
            case PerObject -> internalSavePerObject(statement, data);
            case PerRawObject -> internalSavePerRawObject(statement, data);
            case PerPackage -> internalSavePerPackage(statement, data);
            case ListOfMessages -> internalSaveListOfMessages(statement, data);
            case ListOfObjects -> internalSaveListOfObjects(statement, data);
            case ListOfRawObjects -> internalSaveListOfRawObjects(statement, data);
            case ListOfPackages -> internalSaveListOfPackages(statement, data);
            default -> throw new UnsupportedDataTypeException("Unsupported accumulateMode = " + accumulateMode);
        }
    }

    protected void internalSavePerMessage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final Message<?> message) {
            internalSavePreparedMessage(statement, message);
        } else if (data instanceof final Iterable items) {
            for (@NotNull final var item : items) {
                if (item instanceof final Message<?> message) {
                    internalSavePreparedMessage(statement, message);
                } else {
                    throw new UnsupportedDataTypeException("Unsupported class of element Iterable data. Class of element = " + item.getClass().getName());
                }
            }
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    protected void internalSavePerObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final DataObject dataObject) {
            internalSavePreparedDataObject(statement, dataObject);
        } else if (data instanceof final DataPackage dataPackage) {
            for (@NotNull final var object : dataPackage.getObjects()) {
                internalSavePreparedDataObject(statement, (DataObject) object);
            }
        } else if (data instanceof final Iterable items) {
            for (@NotNull final var item : items) {
                if (item instanceof final DataObject dataObject) {
                    internalSavePreparedDataObject(statement, dataObject);
                } else if (item instanceof final DataPackage dataPackage) {
                    for (@NotNull final var object : dataPackage.getObjects()) {
                        internalSavePreparedDataObject(statement, (DataObject) object);
                    }
                } else {
                    throw new UnsupportedDataTypeException("Unsupported class of element Iterable data. Class of element = " + item.getClass().getName());
                }
            }
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    protected void internalSavePerRawObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException {
        if (data instanceof final Iterable items) {
            for (@NotNull final var item : items) {
                internalSavePreparedRawObject(statement, item);
            }
        } else {
            internalSavePreparedRawObject(statement, data);
        }
    }

    protected void internalSavePerPackage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final DataPackage dataPackage) {
            internalSavePreparedDataPackage(statement, dataPackage);
        } else if (data instanceof final Iterable items) {
            for (@NotNull final var item : items) {
                if (item instanceof final DataPackage dataPackage) {
                    internalSavePreparedDataPackage(statement, dataPackage);
                } else {
                    throw new UnsupportedDataTypeException("Unsupported class of element Iterable data. Class of element = " + item.getClass().getName());
                }
            }
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected void internalSaveListOfMessages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final Message<?> message) {
            internalSavePreparedMessage(statement, message);
        } else if (data instanceof final Iterable items) {
            for (final var item : items) {
                if (!(item instanceof Message<?>)) {
                    throw new UnsupportedDataTypeException("Unsupported class of element Iterable data. Class of element = " + item.getClass().getName());
                }
            }
            internalSavePreparedMessages(statement, items);
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected void internalSaveListOfObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final DataPackage dataPackage) {
            // Сохраняем сразу все объекты dataPackage-а
            internalSavePreparedDataObjects(statement, dataPackage.getObjects());
        } else if (data instanceof final Iterable items) {
            var isAllDataObject = true;
            for (final var item : items) {
                if (!(item instanceof DataObject)) {
                    isAllDataObject = false;
                    break;
                }
            }
            if (isAllDataObject) {
                // Без переупаковки, т.к. все элементы входящего набора являются DataObject
                internalSavePreparedDataObjects(statement, items);
            } else {
                // Требуется переупаковка, т.к. среди элементов существуют не только DataObjects
                final var list = new ArrayList<DataObject>();
                items.forEach(item -> {
                    if (item instanceof final DataObject dataObject) {
                        list.add(dataObject);
                    } else if (item instanceof final DataPackage dataPackage) {
                        list.addAll(dataPackage.getObjects());
                    }
                });
                internalSavePreparedDataObjects(statement, list);
            }
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected void internalSaveListOfRawObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final Iterable items) {
            internalSavePreparedRawObjects(statement, items);
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected void internalSaveListOfPackages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException, JsonProcessingException, UnsupportedDataTypeException {
        if (data instanceof final DataPackage dataPackage) {
            internalSavePreparedDataPackage(statement, dataPackage);
        } else if (data instanceof final Iterable items) {
            for (final var item : items) {
                if (!(item instanceof DataPackage)) {
                    throw new UnsupportedDataTypeException("Expected Iterable<DataPackage>!");
                }
            }
            internalSavePreparedDataPackages(statement, items);
        } else {
            throw new UnsupportedDataTypeException("Unsupported class for parameter data. Class = " + data.getClass().getName());
        }
    }

    protected abstract void internalSavePreparedMessage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Message<?> message
    ) throws SQLException, JsonProcessingException;

    protected abstract void internalSavePreparedMessages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Iterable<Message<?>> messages
    ) throws SQLException, JsonProcessingException;

    protected abstract void internalSavePreparedDataObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final DataObject dataObject
    ) throws SQLException, JsonProcessingException;

    protected abstract void internalSavePreparedRawObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object rawObject
    ) throws SQLException, JsonProcessingException;

    public abstract void internalSavePreparedDataObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<DataObject> dataObjects
    ) throws SQLException, JsonProcessingException;

    public abstract void internalSavePreparedRawObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<Object> rawObjects
    ) throws SQLException, JsonProcessingException;

    public abstract void internalSavePreparedDataPackage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull DataPackage<?> dataPackage
    ) throws SQLException, JsonProcessingException;

    public abstract void internalSavePreparedDataPackages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<DataPackage<?>> dataPackages
    ) throws SQLException, JsonProcessingException;

    protected abstract void executeStatement(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object data
    ) throws SQLException;
}
