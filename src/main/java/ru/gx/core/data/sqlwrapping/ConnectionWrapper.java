package ru.gx.core.data.sqlwrapping;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.sql.SQLException;

@SuppressWarnings("unused")
public interface ConnectionWrapper extends Closeable {
    @NotNull
    Object getInternalConnection();

    @NotNull
    SqlCommandWrapper getQuery(@NotNull String sqlCommand) throws SQLException;

    @NotNull
    SqlCommandWrapper getCallable(@NotNull String sqlCommand) throws SQLException;

    void openTransaction() throws SQLException;

    void commitTransaction() throws SQLException;

    void rollbackTransaction() throws SQLException;
}
