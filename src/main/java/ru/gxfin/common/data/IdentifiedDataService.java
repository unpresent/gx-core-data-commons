package ru.gxfin.common.data;

public interface IdentifiedDataService<ID, O extends IdentifiedDataObject<ID>>
        extends DataService<O> {
    /**
     * Получение объекта по идентификатору
     * @return
     */
    O getById(ID id);

    /**
     * Проверка на существование объекта по Id
     * @param id
     * @return
     */
    boolean existsById(ID id);

    /**
     * Проверка на отсутствие объекта по Id
     * @param id
     * @return
     */
    boolean notExistsById(ID id);
}
