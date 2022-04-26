package ru.gx.core.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@Converter
public class PostgreListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return String.join(",", strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(",")).toList();
    }
}