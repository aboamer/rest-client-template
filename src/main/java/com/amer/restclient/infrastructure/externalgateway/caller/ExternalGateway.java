package com.amer.restclient.infrastructure.externalgateway.caller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

/**
 * Copyright (c) Jumia.
 */
public interface ExternalGateway<RT> {

    /**
     * Calls the external gateway api
     *
     * @param responseClass
     * @return ResponseEntity<RT>
     */
    ResponseEntity<RT> callApi(ExternalGatewayUrl externalGatewayURL, HttpEntity httpEntity, Class<RT> responseClass) throws IOException;
}
