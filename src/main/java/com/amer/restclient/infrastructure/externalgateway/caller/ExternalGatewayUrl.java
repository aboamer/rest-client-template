package com.amer.restclient.infrastructure.externalgateway.caller;

import lombok.Builder;
import lombok.Getter;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.springframework.http.HttpMethod;

import java.nio.charset.StandardCharsets;

/**
 * Copyright (c) Jumia.
 */
@Builder
public class ExternalGatewayUrl {

    @Getter
    private final HttpMethod httpMethod;

    private final String baseApiUrl;

    @Getter
    private final String serviceName;

    protected ExternalGatewayUrl(final HttpMethod httpMethod, final String baseApiUrl, final String serviceName) {

        this.httpMethod = httpMethod;
        this.baseApiUrl = baseApiUrl;
        this.serviceName = serviceName;
    }

    public HttpURL getHttpURL() throws URIException {

        return baseApiUrl.startsWith(String.copyValueOf(HttpsURL.DEFAULT_SCHEME)) ? getSecureHttpUrl(baseApiUrl) : getUnsecureHttpUrl(baseApiUrl);
    }

    private HttpURL getSecureHttpUrl(String url) throws org.apache.commons.httpclient.URIException {

        return new HttpsURL(url, StandardCharsets.UTF_8.name());
    }

    private HttpURL getUnsecureHttpUrl(String url) throws org.apache.commons.httpclient.URIException {

        return new HttpURL(url, StandardCharsets.UTF_8.name());
    }


}
