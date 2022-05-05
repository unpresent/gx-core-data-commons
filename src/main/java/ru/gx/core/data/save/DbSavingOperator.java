package ru.gx.core.data.save;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;

import javax.activation.UnsupportedDataTypeException;
import java.sql.SQLException;

/**
 * Интерфейс оператора сохранения данных в БД.
 */
@SuppressWarnings("unused")
public interface DbSavingOperator {
    /**
     * Подготовка оператора к работе
     *
     * @param sqlCommand     SQL сохранения данных
     * @param accumulateMode режим накопления (объединения) данных перед сохранением
     * @return Statement сохранения
     * @throws SQLException ошибка при работе с БД
     */
    @NotNull
    Object prepareStatement(
            @NotNull final String sqlCommand,
            @NotNull final DbSavingAccumulateMode accumulateMode
    ) throws SQLException;

    /**
     * Сохранение данных в БД
     *
     * @param statement      Statement обращения к БД
     * @param data           данные, в зависимости от {@param accumulateMode} допускаются:
     *                       <ul>
     *                            <li>{@link DataObject}</li>
     *                            <li>Iterable of {@link DataObject}</li>
     *                            <li>{@link DataPackage}</li>
     *                            <li>Iterable of {@link DataPackage}</li>
     *                       </ul>
     * @param accumulateMode режим накопления (объединения) данных перед сохранением
     * @throws SQLException ошибка при работе с БД
     */
    void saveData(
            @NotNull final Object statement,
            @NotNull final Object data,
            @NotNull final DbSavingAccumulateMode accumulateMode
    ) throws SQLException, UnsupportedDataTypeException, JsonProcessingException;
}
