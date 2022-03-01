package com.amer.restclient.infrastructure.rest.test.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Copyright (c) Jumia.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomHeaders {

    private static final String X_REQUEST_ID_HEADER_KEY = "X-Request-Id";

    public static HttpHeaders getHttpHeaders(final String token) {

        final HttpHeaders headers = createBaseHttHeaders();

        headers.setBearerAuth(token);

        return headers;
    }

    public static HttpHeaders getHttpHeaders() {

        return createBaseHttHeaders();
    }

    private static HttpHeaders createBaseHttHeaders() {

        final HttpHeaders headers = new HttpHeaders();

        headers.add(X_REQUEST_ID_HEADER_KEY, TraceIdHolder.get());
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
