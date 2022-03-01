package com.amer.restclient.infrastructure.externalgateway.exception;

/**
 * Copyright (c) Jumia.
 */
public class WrapperRuntimeException extends RuntimeException {

    public WrapperRuntimeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
