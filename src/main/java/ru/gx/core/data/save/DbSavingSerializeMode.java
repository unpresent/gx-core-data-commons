package ru.gx.core.data.save;

/**
 * Способ представления данных при сериализации (отправке данных в БД)
 */
@SuppressWarnings("unused")
public enum DbSavingSerializeMode {
    /**
     * Данные будут переданы в СУБД в виде Json.
     */
    Json,

    /**
     * Данные будут отправлены в СУБД в бинарном виде
     */
    Binary
}
