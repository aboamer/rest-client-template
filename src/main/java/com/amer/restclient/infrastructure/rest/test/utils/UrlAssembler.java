package com.amer.restclient.infrastructure.rest.test.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

/**
 * Copyright (c) Jumia.
 */
@Component
public class UrlAssembler {

    private static final String URL_PATH = "URL path";

    private final String basePath;

    @Autowired
    public UrlAssembler(@Value("${com.apiBaseUrl}") final String basePath) {

        this.basePath = basePath;
    }

    public String assemble(final String path) {

        Preconditions.checkArgument(StringUtils.isNotBlank(path), URL_PATH);

        final UriComponentsBuilder uriBuilder = getUriComponentsBuilder(basePath + "/" + path);

        return uriBuilder.toUriString();
    }

    public String assemble(final String path, final MultiValueMap<String, String> queryParameters) {

        Preconditions.checkArgument(StringUtils.isNotBlank(path), URL_PATH);
        Preconditions.checkArgument(Objects.nonNull(queryParameters), "Query Parameters path");

        final UriComponentsBuilder uriBuilder = getUriComponentsBuilder(basePath + "/" + path);

        return uriBuilder.queryParams(queryParameters).toUriString();
    }

    private static UriComponentsBuilder getUriComponentsBuilder(final String url) {

        return UriComponentsBuilder.fromHttpUrl(url);
    }
}
