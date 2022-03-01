package com.amer.restclient.infrastructure.rest.interceptor.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Copyright (c) Jumia.
 */
@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogExternalRequest implements Serializable {

    private final String id;
    private final String endpoint;
    private final String httpMethod;
    private final String requestHeaders;
    private final String requestBody;
    private final String responseHeaders;
    private final String responseBody;
    private final String httpStatus;
    private final Integer responseCode;
    private final String statusText;
    private final String time;
    private final Long responseTime;
    private final String exception;
    private final String service;

    @JsonProperty("requestId")
    private final String traceId;
}
