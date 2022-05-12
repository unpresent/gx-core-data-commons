package ru.gx.core.data.sqlwrapping;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;

@SuppressWarnings("unused")
public interface ThreadConnectionsWrapper {
    @NotNull
    ConnectionWrapper getCurrentThreadConnection() throws SQLException, IOException;
}
