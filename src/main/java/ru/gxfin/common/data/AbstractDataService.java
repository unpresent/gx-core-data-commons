package ru.gxfin.common.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class AbstractDataService<O extends AbstractDataObject, P extends AbstractDataPackage<O>, ID, REPO
        extends JpaRepository<O, ID>>
        implements DataService<O> {

    private final ObjectMapper objectMapper;

    public AbstractDataService(ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    public abstract Class<P> getPackageClass();

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private REPO repository;

    @Override
    public void add(O dataObject) {
        repository.save(dataObject);
    }

    @Override
    public void addPackage(DataPackage<O> dataPackage) {
        repository.saveAll(dataPackage.getItems());
    }

    @Override
    public DataPackage<O> addJsonPackage(String jsonPackage) throws JsonProcessingException {
        final var dataPackage = this.objectMapper.readValue(jsonPackage, getPackageClass());
        addPackage(dataPackage);
        return dataPackage;
    }

    @Override
    public void update(O dataObject) {
        repository.save(dataObject);
    }

    @Override
    public void delete(O dataObject) {
        repository.delete(dataObject);
    }
}
