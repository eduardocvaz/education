package com.labcomu.edu.exceptions;

import java.io.Serial;

public class CircuitBreakerOpenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public CircuitBreakerOpenException() {

    }

    public CircuitBreakerOpenException(String msg) {
        super(msg);
    }
}
