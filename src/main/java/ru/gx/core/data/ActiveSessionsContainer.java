package ru.gx.core.data;

import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ActiveSessionsContainer {
    @NotNull
    private final Map<Thread, Session> sessions = new HashMap<>();

    @Nullable
    public synchronized Session get(@NotNull final Thread thread) {
        return this.sessions.get(thread);
    }

    @Nullable
    public Session getCurrent() {
        return get(Thread.currentThread());
    }

    public synchronized void put(@NotNull final Thread thread, @Nullable final Session session) {
        if (session == null) {
            this.sessions.remove(thread);
        } else {
            this.sessions.put(thread, session);
        }
    }

    public void putCurrent(@Nullable final Session session) {
        this.put(Thread.currentThread(), session);
    }
}
