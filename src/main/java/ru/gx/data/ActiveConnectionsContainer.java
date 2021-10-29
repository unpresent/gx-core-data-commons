package ru.gx.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ActiveConnectionsContainer {
    @NotNull
    private final Map<Thread, Connection> connections = new HashMap<>();

    @Nullable
    public synchronized Connection get(@NotNull final Thread thread) {
        return this.connections.get(thread);
    }

    @Nullable
    public Connection getCurrent() {
        return get(Thread.currentThread());
    }

    public synchronized void put(@NotNull final Thread thread, @Nullable final Connection connection) {
        if (connection == null) {
            this.connections.remove(thread);
        } else {
            this.connections.put(thread, connection);
        }
    }

    public void putCurrent(@Nullable final Connection connection) {
        this.put(Thread.currentThread(), connection);
    }
}
