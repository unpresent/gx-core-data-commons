package ru.gx.core.data.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import ru.gx.core.data.TranslatableString;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

@SuppressWarnings("unused")
public class PostgreSQLTranslatableStringJsonType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<?> returnedClass() {
        return TranslatableString.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }

        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @SneakyThrows
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (rs.getString(names[0]) == null) {
            return null;
        }
        return new ObjectMapper().readValue(rs.getString(names[0]), TranslatableString.class);
    }

    @SneakyThrows
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        st.setObject(index, new ObjectMapper().writeValueAsString(value), Types.OTHER);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (TranslatableString) this.deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return this.deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }
}
