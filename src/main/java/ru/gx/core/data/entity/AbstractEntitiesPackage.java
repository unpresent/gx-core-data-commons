package ru.gx.core.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AbstractEntitiesPackage<E extends AbstractEntityObject> implements EntitiesPackage<E> {
    @Getter(AccessLevel.PROTECTED)
    private final List<E> listObjects = new ArrayList<>();

    @Override
    @NotNull
    public Collection<E> getObjects() {
        return this.listObjects;
    }

    @Override
    @Nullable
    public E get(int index) {
        return getListObjects().get(index);
    }

    @Override
    public int size() {
        return getListObjects().size();
    }
}
