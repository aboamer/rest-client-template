package com.amer.restclient.infrastructure.rest.test;

import com.amer.restclient.infrastructure.externalgateway.AbstractRestGateway;
import com.amer.restclient.infrastructure.externalgateway.caller.ExternalGatewayUrl;
import com.amer.restclient.infrastructure.externalgateway.restclientwrapper.ExternalRequestClient;
import com.amer.restclient.infrastructure.rest.test.request.RequestDto;
import com.amer.restclient.infrastructure.rest.test.response.ResponseDto;
import com.amer.restclient.infrastructure.rest.test.utils.CustomHeaders;
import com.amer.restclient.infrastructure.rest.test.utils.UrlAssembler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Copyright (c) Jumia.
 */
@Service
@Log4j2
public class RestGatewayImpl extends AbstractRestGateway<RequestDto, ResponseDto> {

    private final UrlAssembler urlAssembler;

    @Autowired
    public RestGatewayImpl(final ExternalRequestClient externalRequestClient, final UrlAssembler urlAssembler) {

        super(externalRequestClient, ResponseDto.class);
        this.urlAssembler = urlAssembler;
    }

    @Override
    protected String getServiceName() {

        return "first service";
    }

    @Override
    protected ExternalGatewayUrl buildExternalGatewayUrl(RequestDto requestType) {

        final MultiValueMap<String, String> queryParameters = buildQueryParameters(requestType);

        return ExternalGatewayUrl.builder()
                .serviceName(getServiceName()).httpMethod(HttpMethod.GET)
                .baseApiUrl(urlAssembler.assemble("api/users", queryParameters)).build();
    }

    @Override
    protected HttpEntity<RequestDto> buildHttpEntity(RequestDto requestType) throws JsonProcessingException {

        final HttpHeaders headers = CustomHeaders.getHttpHeaders();
//        headers.setBasicAuth(externalAuthenticationRequestDto.getMerchantClientId(), externalAuthenticationRequestDto.getApiSecretKey(), StandardCharsets.UTF_8);

        return new HttpEntity<>(requestType, headers);
    }

    @Override
    protected HttpStatus getExpectedHttpStatus() {

        return HttpStatus.CREATED;
    }

    @Override
    protected void performCustomErrorChecking(RequestDto requestType, ResponseDto responseType) {

    }

    private MultiValueMap<String, String> buildQueryParameters(final RequestDto requestDto) {

        final MultiValueMap<String, String> queryParameters = new LinkedMultiValueMap<>();
        queryParameters.add("page", requestDto.getPage());

        return queryParameters;
    }
}
