package com.amer.restclient.infrastructure.rest.interceptor;

import com.amer.restclient.core.utils.Constants;
import com.amer.restclient.infrastructure.rest.interceptor.factory.LogExternalRequestFactory;
import com.amer.restclient.infrastructure.rest.interceptor.request.LogExternalRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) Jumia.
 */
@Log4j2
@Component
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        final ClientHttpResponse response;

        final String id = "" + UUID.randomUUID();

        final String traceId = getRequestValueFromHeader(request.getHeaders());

        logIncomingHttpRequest(request, body, id, traceId);

        final long requestStartTime = System.nanoTime();

        final long requestDuration;

        try {

            response = execution.execute(request, body);

            requestDuration = calculateRequestDuration(requestStartTime);

        } catch (final Exception ex) {

            logOutgoingHttpResponse(ex.getMessage(), calculateRequestDuration(requestStartTime), id, traceId);

            throw new IOException(traceId + " " + ex.getMessage(), ex);
        }

        logOutgoingHttpResponse(response, requestDuration, id, traceId);

        return response;
    }

    private void logOutgoingHttpResponse(final String message, final Long responseTime, final String id, final String traceId) {

        final LogExternalRequest externalGatewayLog = LogExternalRequestFactory.create(message, responseTime, id, traceId);

        sendMessage(externalGatewayLog);
    }

    private void logOutgoingHttpResponse(final ClientHttpResponse response, final Long responseTime, final String id, final String traceId) throws IOException {

        final LogExternalRequest externalGatewayLog = LogExternalRequestFactory.create(response, responseTime, id, traceId);

        sendMessage(externalGatewayLog);
    }

    private long calculateRequestDuration(final long requestStartTime) {

        return System.currentTimeMillis() - requestStartTime;
    }

    private void logIncomingHttpRequest(final HttpRequest request, final byte[] body, final String id, final String traceId) {

        final LogExternalRequest externalGatewayLog = LogExternalRequestFactory.create(request, body, id, traceId);

        sendMessage(externalGatewayLog);
    }

    private void sendMessage(final LogExternalRequest externalGatewayLog) {

        try {

            log.info(externalGatewayLog);

        } catch (final Exception ex) {

            log.warn("{} - Unable to publish message {} due to {}", externalGatewayLog.getTraceId(), externalGatewayLog, ex);
        }
    }

    private String getRequestValueFromHeader(final HttpHeaders headers) {

        if (!headers.isEmpty()
                && headers.containsKey(Constants.REQUEST_ID_HEADER_KEY)
                && CollectionUtils.isNotEmpty(headers.get(Constants.REQUEST_ID_HEADER_KEY))
        ) {
            final List<String> values = headers.get(Constants.REQUEST_ID_HEADER_KEY);

            return CollectionUtils.isNotEmpty(values) ? values.get(0) : StringUtils.EMPTY;
        }

        return StringUtils.EMPTY;
    }
}
