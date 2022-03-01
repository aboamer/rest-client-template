package com.amer.restclient.infrastructure.externalgateway.responsefactory;

import com.amer.restclient.infrastructure.externalgateway.exception.WrapperRuntimeException;

import java.text.MessageFormat;

/**
 * Copyright (c) Jumia.
 */
public class ResponseTypeInstanceFactory<RT> {

    private static final String REFLECTION_EXCEPTION = "An error has occurred building an instance of {}: {} - {}";

    private static final String CLASS_CANNOT_BE_NULL_MSG = "responseType Class in createResponseTypeInstance(final Class<RT> responseType) cannot be null";

    public RT createResponseTypeInstance(final Class<RT> responseType) {

        validateInput(responseType);

        try {
            return responseType.getDeclaredConstructor().newInstance();

        } catch (final ReflectiveOperationException exception) {

            throw new WrapperRuntimeException(MessageFormat.format(REFLECTION_EXCEPTION, responseType, exception.getMessage()), exception);
        }
    }

    private void validateInput(final Class<RT> responseType) {

        if (responseType == null) {
            throw new IllegalArgumentException(CLASS_CANNOT_BE_NULL_MSG);
        }
    }
}
