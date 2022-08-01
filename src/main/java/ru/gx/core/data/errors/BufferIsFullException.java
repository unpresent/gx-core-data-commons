package ru.gx.core.data.errors;

public class BufferIsFullException extends RuntimeException {
    public BufferIsFullException(String message) {
        super(message);
    }
}
