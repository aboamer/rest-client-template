package com.amer.restclient.infrastructure.rest.interceptor.factory;

import com.amer.restclient.core.utils.Constants;
import com.amer.restclient.infrastructure.rest.interceptor.request.LogExternalRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Copyright (c) Jumia.
 */
@Log4j2
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LogExternalRequestFactory {

    private static final String NO_CONTENT = "No content available";
    private static final int NO_STATUS_CODE = -1;
    private static final String PREFIX = "amer-service";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN);

    public static LogExternalRequest create(final HttpRequest request, final byte[] body, final String id, final String traceId) {

        final HttpMethod method = request.getMethod();

        return LogExternalRequest.builder()
                .id(id)
                .traceId(traceId)
                .requestBody(mask(convertBodyToString(body, traceId)))
                .requestHeaders(mask(convertRequestHeader(request)))
                .httpMethod((method != null) ? method.name() : null)
                .endpoint(getEndpoint(request))
                .service(PREFIX)
                .time(getCurrentFormattedTime())
                .build();
    }

    public static LogExternalRequest create(final String message, final Long responseTime, final String id, final String traceId) {

        return LogExternalRequest.builder()
                .id(id)
                .traceId(traceId)
                .exception(message)
                .time(getCurrentFormattedTime())
                .responseTime(responseTime)
                .service(PREFIX)
                .build();
    }

    public static LogExternalRequest create(final ClientHttpResponse response, final Long responseTime, final String id, final String traceId) throws IOException {

        return LogExternalRequest.builder()
                .id(id)
                .traceId(traceId)
                .responseBody(mask(convertResponseBody(response)))
                .responseHeaders(mask(convertResponseHeader(response)))
                .statusText(response != null ? response.getStatusText() : NO_CONTENT)
                .responseCode(response != null ? response.getStatusCode().value() : NO_STATUS_CODE)
                .time(getCurrentFormattedTime())
                .responseTime(responseTime)
                .service(PREFIX)
                .build();
    }

    private static String convertBodyToString(final byte[] body, final String traceId) {

        if (body != null && body.length > 0) {

            try {

                return new String(body, StandardCharsets.UTF_8.name());

            } catch (final UnsupportedEncodingException e) {

                log.warn("{} - It was not possible to convert body", traceId, e);
            }
        }

        return null;
    }

    private static String getEndpoint(final HttpRequest request) {

        return request.getURI().toString();
    }

    private static String getCurrentFormattedTime() {

        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    private static String convertResponseBody(final ClientHttpResponse response) throws IOException {

        if (response != null) {

            return IOUtils.toString(response.getBody(), Charset.defaultCharset());
        }

        return NO_CONTENT;
    }

    private static String convertResponseHeader(final ClientHttpResponse response) {

        if (response != null) {

            return response.getHeaders().toString();

        }

        return NO_CONTENT;
    }

    private static String convertRequestHeader(final HttpRequest request) {

        if (request != null) {

            return request.getHeaders().toString();

        }

        return NO_CONTENT;
    }

    private static String mask(final String input) {

//        return StringMasker.mask(input, LoggingMasquerader.getMasks());
        return input;
    }
}
