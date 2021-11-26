package ru.gx.core.data.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@SuppressWarnings("unused")
public class PhysicalNamingStrategyQuotedImpl implements PhysicalNamingStrategy, Serializable {
    public static final PhysicalNamingStrategyQuotedImpl INSTANCE = new PhysicalNamingStrategyQuotedImpl();

    @Override
    @Nullable
    public Identifier toPhysicalCatalogName(@Nullable final Identifier name, @Nullable final JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    @Nullable
    public Identifier toPhysicalSchemaName(@Nullable final Identifier name, @Nullable final JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    @Nullable
    public Identifier toPhysicalTableName(@Nullable final Identifier name, @Nullable final JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    @Nullable
    public Identifier toPhysicalSequenceName(@Nullable final Identifier name, @Nullable final JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    @NotNull
    public Identifier toPhysicalColumnName(@NotNull final Identifier name, @Nullable final JdbcEnvironment jdbcEnvironment) {
        return name.isQuoted() ? name : Identifier.quote(name);
    }
}
