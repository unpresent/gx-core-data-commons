package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.sqlwrapping.SqlCommandWrapper;
import ru.gx.core.messaging.Message;

import java.sql.SQLException;

@SuppressWarnings("unused")
@Accessors(chain = true)
public abstract class AbstractJsonDbSavingOperator
        extends AbstractDbSavingOperator {

    protected AbstractJsonDbSavingOperator(
            @NotNull final ObjectMapper objectMapper
    ) {
        super(objectMapper);
    }

    @Override
    protected void internalSavePreparedMessage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Message<?> message
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(message);
        executeStatement(statement, data);
    }

    @Override
    protected void internalSavePreparedMessages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Iterable<Message<?>> messages
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(messages);
        executeStatement(statement, data);
    }

    @Override
    protected void internalSavePreparedDataObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final DataObject dataObject
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(dataObject);
        executeStatement(statement, data);
    }

    @Override
    protected void internalSavePreparedRawObject(
            @NotNull final SqlCommandWrapper statement,
            @NotNull final Object rawObject
    ) throws SQLException {
        executeStatement(statement, rawObject);
    }

    @Override
    public void internalSavePreparedDataObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<DataObject> dataObjects
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(dataObjects);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedRawObjects(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<Object> rawObjects
    ) throws SQLException {
        final var data = new StringBuilder().append("[");
        var isFirst = true;
        for (final var item: rawObjects) {
            if (!isFirst) {
                data.append(',');
            } else {
                isFirst = false;
            }
            data.append(item);
        }
        data.append(']');
        executeStatement(statement, data.toString());
    }

    @Override
    public void internalSavePreparedDataPackage(
            @NotNull final SqlCommandWrapper statement,
            @NotNull DataPackage<?> dataPackage
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(dataPackage);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedDataPackages(
            @NotNull final SqlCommandWrapper statement,
            @NotNull Iterable<DataPackage<?>> dataPackages
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsString(dataPackages);
        executeStatement(statement, data);
    }
}
