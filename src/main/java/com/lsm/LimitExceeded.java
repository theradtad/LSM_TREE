package com.lsm;

public class LimitExceeded extends Exception {
    public LimitExceeded(String message) {
        super(message);
    }
}
