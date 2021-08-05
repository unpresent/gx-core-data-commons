package ru.gxfin.common.data;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AbstractEntitiesPackage<E extends AbstractEntityObject> implements EntitiesPackage<E> {
    @Getter(AccessLevel.PROTECTED)
    private final List<E> listObjects = new ArrayList<>();

    @Override
    public Collection<E> getObjects() {
        return this.listObjects;
    }

    @Override
    public E get(int index) {
        return getListObjects().get(index);
    }

    @Override
    public int size() {
        return getListObjects().size();
    }
}
