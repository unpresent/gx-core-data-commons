package ru.gx.core.data.save;

/**
 * Метод обработки данных.
 */
@SuppressWarnings("unused")
public enum DbSavingProcessMode {
    /**
     * Сразу при получении данных
     */
    Immediate,

    /**
     * Допускается буферизация данных
     */
    UseBuffer
}
