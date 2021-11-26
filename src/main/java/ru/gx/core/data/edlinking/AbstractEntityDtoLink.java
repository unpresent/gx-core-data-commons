package ru.gx.core.data.edlinking;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.repository.CrudRepository;
import ru.gx.core.data.DataMemoryRepository;
import ru.gx.core.data.DataObject;
import ru.gx.core.data.DataPackage;
import ru.gx.core.data.entity.EntitiesPackage;
import ru.gx.core.data.entity.EntityObject;

import java.lang.reflect.InvocationTargetException;

@Getter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class AbstractEntityDtoLink<E extends EntityObject, EP extends EntitiesPackage<E>, ID, O extends DataObject, P extends DataPackage<O>>
        implements EntityDtoLinkDescriptor<E, EP, ID, O, P> {

    @NotNull
    private final Class<E> entityClass;

    @Setter
    @Nullable
    private Class<EP> entitiesPackageClass;

    @NotNull
    private final Class<O> dtoClass;

    @Setter
    @Nullable
    private Class<P> dtoPackageClass;

    @Setter
    @Nullable
    private CrudRepository<E, ID> repository;

    @Setter
    @Nullable
    private DataMemoryRepository<O, P> memoryRepository;

    @Setter
    @Nullable
    private EntityFromDtoConvertor<E, O> entityFromDtoConverter;

    @Setter
    @Nullable
    private DtoFromEntityConvertor<O, E> dtoFromEntityConverter;

    protected AbstractEntityDtoLink(@NotNull Class<E> entityClass, @NotNull Class<O> dtoClass) {
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @SneakyThrows({NoSuchMethodException.class, InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    @Override
    public @NotNull P createDtoPackage() throws EntitiesDtoLinksConfigurationException {
        if (this.dtoPackageClass == null) {
            throw new EntitiesDtoLinksConfigurationException("Can't create DTO package due undefined dtoPackageClass! dtoClass = " + this.dtoClass.getName());
        }
        final var constructor = this.dtoPackageClass.getConstructor();
        return constructor.newInstance();
    }

    @SneakyThrows({NoSuchMethodException.class, InvocationTargetException.class, InstantiationException.class, IllegalAccessException.class})
    @Override
    public @NotNull EP createEntitiesPackage() throws EntitiesDtoLinksConfigurationException {
        if (this.entitiesPackageClass == null) {
            throw new EntitiesDtoLinksConfigurationException("Can't create Entities package due undefined entitiesPackageClass! entitiyClass = " + this.entityClass.getName());
        }
        final var constructor = this.entitiesPackageClass.getConstructor();
        return constructor.newInstance();
    }
}
