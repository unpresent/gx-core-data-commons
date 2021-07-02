package ru.gxfin.common.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractDataService<T extends AbstractDataObject, ID, REPO extends JpaRepository<T, ID>> implements DataService<T, ID> {
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private REPO repository;

    @Override
    public void add(T dataObject) {
        repository.save(dataObject);
    }

    @Override
    public void addAll(DataPackage<T> dataPackage) {
        repository.saveAll(dataPackage.getItems());
    }

    @Override
    public T getById(ID id) {
        return repository.getById(id);
    }

    @Override
    public void update(T dataObject) {
        repository.save(dataObject);
    }

    @Override
    public void delete(T dataObject) {
        repository.delete(dataObject);
    }
}
