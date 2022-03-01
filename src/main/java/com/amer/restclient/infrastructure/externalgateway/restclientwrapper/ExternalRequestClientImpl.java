package com.amer.restclient.infrastructure.externalgateway.restclientwrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * a wrapper to resttemplate object, that is provided by whatever app that extends AbstractRestGateway (via spring DI)
 * will be injected in the service that extends AbstractRestGateway - so it should be annotated
 */
@Component
public class ExternalRequestClientImpl implements ExternalRequestClient {

    private RestTemplate restTemplate;

    @Autowired
    public ExternalRequestClientImpl(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @Override
    public Object getClient() {

        return restTemplate;
    }
}