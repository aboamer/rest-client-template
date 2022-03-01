package com.amer.restclient.infrastructure.externalgateway.caller;

import com.amer.restclient.core.utils.JsonUtils;
import com.amer.restclient.infrastructure.externalgateway.restclientwrapper.ExternalRequestClient;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * here is the actual API call + conversions from raw to object response
 * it uses JsonUtils.getObjectMapper to convert raw response to object
 * it gets the restTemplate instance from ExternalRequestClient, provided by AbstractRestGateway implementation
 */
@NoArgsConstructor
public class ExternalGatewayImpl<RT> implements ExternalGateway<RT> {

    private RestTemplate restTemplate;

    public ExternalGatewayImpl(final ExternalRequestClient externalRequestClient) {

        this.restTemplate = (RestTemplate) externalRequestClient.getClient();
    }

    @Override
    public ResponseEntity<RT> callApi(ExternalGatewayUrl externalGatewayURL, HttpEntity httpEntity, Class<RT> responseClass) throws IOException {

        ResponseEntity<String> rawResponseEntity = restTemplate
                .exchange(externalGatewayURL.getHttpURL().getURI(), externalGatewayURL.getHttpMethod(), httpEntity, String.class);

        return convertRawResponseEntityToResponseEntity(rawResponseEntity, responseClass);
    }

    private ResponseEntity<RT> convertRawResponseEntityToResponseEntity(ResponseEntity<String> rawResponseEntity,
                                                                        Class<? extends RT> responseClass) throws IOException {

        return new ResponseEntity<>(mapRawResponse(rawResponseEntity.getBody(), responseClass), rawResponseEntity.getStatusCode());
    }

    private RT mapRawResponse(String rawBody, Class<? extends RT> responseClass) throws IOException {

        if (StringUtils.isBlank(rawBody)) {
            rawBody = "{}";
        }

        return JsonUtils.getObjectMapper().readValue(rawBody, responseClass);
    }
}
