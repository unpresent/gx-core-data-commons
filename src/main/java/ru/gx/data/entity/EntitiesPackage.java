package ru.gx.data.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings({"unused"})
public interface EntitiesPackage<O extends EntityObject> {
    @NotNull
    Collection<O> getObjects();

    @Nullable
    O get(int index);

    int size();
}
