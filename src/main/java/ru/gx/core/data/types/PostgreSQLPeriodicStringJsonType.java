package ru.gx.core.data.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.SneakyThrows;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import ru.gx.core.periodic.PeriodicKeyDeserializer;
import ru.gx.core.periodic.PeriodicString;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class PostgreSQLPeriodicStringJsonType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class<?> returnedClass() {
        return PeriodicString.class;
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
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addKeyDeserializer(LocalDate.class, new PeriodicKeyDeserializer());
        mapper.registerModule(simpleModule);

        return mapper.readValue(rs.getString(names[0]), PeriodicString.class);
    }

    @SneakyThrows
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }

        if (((PeriodicString) value).isEmpty()) {
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
        return (PeriodicString) this.deepCopy(value);
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
