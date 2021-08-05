package ru.gxfin.common.data;

import java.util.Collection;

@SuppressWarnings({"unused"})
public interface EntitiesPackage<O extends EntityObject> {
    Collection<O> getObjects();
    O get(int index);
    int size();
}
