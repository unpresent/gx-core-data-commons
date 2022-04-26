package ru.gx.core.data.variant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ToString
@Getter
@EqualsAndHashCode
public class Variant implements Serializable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @JsonProperty("type")
    @NotNull
    private VariantType type;

    @JsonIgnore
    @Nullable
    private final Object value;

    @SuppressWarnings("unused")
    @JsonProperty("value")
    private String serializeValue() {
        if (value == null)
            return null;

        String result;

        switch (type) {
            case DATE -> result = ((LocalDate) value).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            case DATETIME -> result = ((LocalDateTime) value).format(DateTimeFormatter.ofPattern(DATETIME_FORMAT));
            case TIME -> result = ((LocalTime) value).format(DateTimeFormatter.ofPattern(TIME_FORMAT));

            default -> result = value.toString();
        }

        return result;
    }

    @SuppressWarnings("UnnecessaryDefault")
    private Object deserializeValue(@NotNull String stringValue) {
        return switch (type) {
            case STRING -> stringValue;
            case DATE -> LocalDate.parse(stringValue, DateTimeFormatter.ofPattern(DATE_FORMAT));
            case TIME -> LocalTime.parse(stringValue, DateTimeFormatter.ofPattern(TIME_FORMAT));
            case DATETIME -> LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern(DATETIME_FORMAT));
            case INTEGER -> Integer.parseInt(stringValue);
            case SHORT -> Short.parseShort(stringValue);
            case LONG -> Long.parseLong(stringValue);
            case BIGDECIMAL -> new BigDecimal(stringValue);
            default -> throw new RuntimeException(String.format("Unknown data type [%s]", type));
        };
    }

    @JsonCreator
    public Variant(
            @JsonProperty("type") @NotNull VariantType type,
            @JsonProperty("value") @Nullable String stringValue) {
        this.type = type;
        this.value = stringValue == null ? null : deserializeValue(stringValue);
    }
}
