package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Интерфейс серевиса-хранилища объектов.
 */
public interface DataService<O extends DataObject> {

    /**
     * Добавление одного объекта.
     * @param dataObject добавляемый объект в Хранилище.
     */
    void add(O dataObject);

    /**
     * Добавление пакета объектов.
     * @param dataPackage добавляемый пакет объектов в Хранилище.
     */
    void addPackage(DataPackage<O> dataPackage);


    /**
     * Добавление пакета объектов.
     * @param jsonPackage добавляемый пакет (в виде Json) объектов в Хранилище.
     */
    DataPackage<O> addJsonPackage(String jsonPackage) throws JsonProcessingException;

    /**
     * Обновление объекта в хранилище.
     * @param dataObject обновляемый объект.
     */
    void update(O dataObject);

    /**
     * Удаление объекта из хранилища.
     * @param dataObject удаляемый объект.
     */
    void delete(O dataObject);
}
