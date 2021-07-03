package ru.gxfin.common.data;

/**
 * Интерфейс серевиса-хранилища объектов, который ассоциирован с топиками (входящим и исходящим).
 */
public interface DataServiceWithTopics<O extends DataObject> extends DataService<O> {
    /**
     * @return Входящий топик сообщений, которые будут переданы в данный сервис.
     */
    String incomeTopic();

    /**
     * @return Исходящий топик сообщений, в который будут переданы объекты из данного сервиса.
     */
    String outcomeTopic();
}
