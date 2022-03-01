package com.amer.restclient.infrastructure.rest.test.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Copyright (c) Jumia.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceIdHolder {

    private static final ThreadLocal<String> TRACE_IDS = new ThreadLocal<>();

    public static void set(final String traceId) {

        TRACE_IDS.set(traceId);
    }

    public static String get() {

        return TRACE_IDS.get();
    }

    public static String clear() {

        final String existingTraceId = TRACE_IDS.get();

        TRACE_IDS.remove();

        return existingTraceId;
    }
}
