package com.amer.restclient.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Copyright (c) Jumia.
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = null;

    public static ObjectMapper getObjectMapper() {

        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public static String writeValueAsCompactString(String json) throws IOException {

        ObjectMapper objectMapper = getObjectMapper();
        Object genericJson = objectMapper.readValue(json, Object.class);
        return objectMapper.writer().writeValueAsString(genericJson);
    }
}
