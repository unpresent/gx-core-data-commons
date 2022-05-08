package ru.gx.core.data.sqlwrapping;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface ResultWrapper {
    @NotNull
    Object getInternalData();

    boolean next() throws SQLException;

    boolean first() throws SQLException;

    boolean last() throws SQLException;

    @Nullable
    String getString(int columnIndex) throws SQLException;

    @Nullable
    Integer getInteger(int columnIndex) throws SQLException;

    @Nullable
    Long getLong(int columnIndex) throws SQLException;

    @Nullable
    BigDecimal getNumeric(int columnIndex) throws SQLException;
}
