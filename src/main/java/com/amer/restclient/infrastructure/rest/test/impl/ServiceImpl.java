package com.amer.restclient.infrastructure.rest.test.impl;

import com.amer.restclient.infrastructure.rest.test.RestGatewayImpl;
import com.amer.restclient.infrastructure.rest.test.request.RequestDto;
import com.amer.restclient.infrastructure.rest.test.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Copyright (c) Jumia.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ServiceImpl {

    private final RestGatewayImpl restGateway;

    public ResponseDto callApi(String page) {

        RequestDto requestDto = RequestDto.builder().page(page).build();

        return restGateway.callAPI(requestDto);
    }
}
