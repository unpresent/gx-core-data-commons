package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.messaging.Message;

import java.sql.SQLException;

@Accessors(chain = true)
public abstract class AbstractBinaryDbSavingOperator
        extends AbstractDbSavingOperator {

    protected AbstractBinaryDbSavingOperator(
            @NotNull final ObjectMapper objectMapper
    ) {
        super(objectMapper);
    }

    @Override
    protected void internalSavePreparedMessage(
            @NotNull final Object statement,
            @NotNull final Message<?> message
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(message);
        executeStatement(statement, data);
    }

    @Override
    protected void internalSavePreparedMessages(
            @NotNull final Object statement,
            @NotNull final Iterable<Message<?>> messages
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(messages);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedDataObject(
            @NotNull final Object statement,
            @NotNull final DataObject dataObject
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(dataObject);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedDataObjects(
            @NotNull final Object statement,
            @NotNull Iterable<DataObject> dataObjects
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(dataObjects);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedDataPackage(
            @NotNull final Object statement,
            @NotNull DataPackage<?> dataPackage
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(dataPackage);
        executeStatement(statement, data);
    }

    @Override
    public void internalSavePreparedDataPackages(
            @NotNull final Object statement,
            @NotNull Iterable<DataPackage<?>> dataPackages
    ) throws SQLException, JsonProcessingException {
        final var data = getObjectMapper().writeValueAsBytes(dataPackages);
        executeStatement(statement, data);
    }
}
