package ru.gxfin.common.data.naming;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;

public class PhysicalNamingStrategyQuotedImpl implements PhysicalNamingStrategy, Serializable {
    public static final PhysicalNamingStrategyQuotedImpl INSTANCE = new PhysicalNamingStrategyQuotedImpl();

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        return name.isQuoted() ? name : Identifier.quote(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return name.isQuoted() ? name : Identifier.quote(name);
    }
}
