package com.lsm;

import java.io.Serializable;

public class WALEntry implements Serializable {
    public enum Operation {
        PUT
    }

    private final Operation operation;
    private final String key;
    private final String value;
    private final long timestamp;

    public WALEntry(Operation operation, String key, String value) {
        this.operation = operation;
        this.key = key;
        this.value = value;
        this.timestamp = System.currentTimeMillis();
    }

    public Operation getOperation() {
        return operation;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("WALEntry{op=%s, key='%s', value='%s', ts=%d}",
                           operation, key, value, timestamp);
    }
}
