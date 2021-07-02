package ru.gxfin.common.data;

/**
 * Интерфейс сереевис-хранилища объектов.
 */
public interface DataService<T extends DataObject, ID> {

    /**
     * Добавление одного объекта.
     * @param dataObject добавляемый объект в Хранилище.
     */
    void add(T dataObject);

    /**
     * Добавление пакета объектов.
     * @param dataPackage добавляемый пакет объектов в Хранилище.
     */
    void addAll(DataPackage<T> dataPackage);

    /**
     * Поиск объекта.
     * @param id идентификатор объекта.
     * @return объект.
     */
    T getById(ID id);

    /**
     * Обновление объекта в хранилище.
     * @param dataObject обновляемый объект.
     */
    void update(T dataObject);

    /**
     * Удаление объекта из хранилища.
     * @param dataObject удаляемый объект.
     */
    void delete(T dataObject);
}
