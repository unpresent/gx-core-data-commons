package ru.gx.core.data.sqlwrapping;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface SqlCommandWrapper extends Closeable {
    @NotNull
    Object getInternalCommand();

    void setStringParam(int paramIndex, @Nullable String value) throws SQLException;
    void setIntegerParam(int paramIndex, @Nullable Integer value) throws SQLException;
    void setLongParam(int paramIndex, @Nullable Long value) throws SQLException;
    void setNumericParam(int paramIndex, @Nullable BigDecimal value) throws SQLException;

    void executeNoResult() throws SQLException;

    ResultWrapper executeWithResult() throws SQLException;
}
