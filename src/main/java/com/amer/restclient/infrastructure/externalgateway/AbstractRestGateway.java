package com.amer.restclient.infrastructure.externalgateway;

import com.amer.restclient.infrastructure.externalgateway.caller.ExternalGateway;
import com.amer.restclient.infrastructure.externalgateway.caller.ExternalGatewayImpl;
import com.amer.restclient.infrastructure.externalgateway.caller.ExternalGatewayUrl;
import com.amer.restclient.infrastructure.externalgateway.restclientwrapper.ExternalRequestClient;
import com.amer.restclient.infrastructure.externalgateway.responsefactory.ResponseTypeInstanceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Optional;

/**
 * this is our framework to handle external api calls
 * any class with any request/response model should extend this class to make sure it makes ordered steps before and after calling
 * it delegates the actual API call to RestHttpGateway, the implementation of ExternalGateway + some conversions from raw to object response
 * it passes externalRequestClient to RestHttpGateway, where externalRequestClient has a method to get what rest client should RestHttpGateway use to call
 * here is an abstract class, an abstract class isn't component-scanned
 */
@Log4j2
public abstract class AbstractRestGateway<CT, RT> {

    private static final String UNEXPECTED_HTTP_RESPONSE = "API was expected to respond with an {}-{} but instead responded with an {}";

    private ResponseTypeInstanceFactory<RT> responseTypeInstanceFactory;

    private Class<RT> responseType;

    private ExternalGateway<RT> restHttpGateway;

    public AbstractRestGateway(final ExternalRequestClient externalRequestClient, final Class<RT> responseType) {

        restHttpGateway = new ExternalGatewayImpl<>(externalRequestClient);
        this.responseType = responseType;
        responseTypeInstanceFactory = new ResponseTypeInstanceFactory<>();
    }

    public RT callAPI(final CT requestData) {

        RT response = responseTypeInstanceFactory.createResponseTypeInstance(responseType);

        try {

            final Optional<RT> serverResponse = callExternalGatewayApi(requestData);

            if (serverResponse.isPresent()) {

                response = serverResponse.get();

            } else {

                log.warn("no response returned");
            }
        } catch (HttpMessageNotReadableException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return response;
    }

    private Optional<RT> callExternalGatewayApi(final CT requestData) throws ReflectiveOperationException, IOException {

        final ExternalGatewayUrl externalGatewayUrl = buildExternalGatewayUrl(requestData);
        final HttpEntity<?> httpEntity = buildHttpEntity(requestData);

        final ResponseEntity<RT> externalGatewayResponse;

        try {
            externalGatewayResponse = restHttpGateway.callApi(externalGatewayUrl, httpEntity, responseType);
            logResponseIfUnexpected(externalGatewayResponse);

        } catch (RestClientException exception) {

            exception.printStackTrace();

            return Optional.empty();
        }

        RT responseBody = externalGatewayResponse.getBody();

        performCustomErrorChecking(requestData, responseBody);

        return Optional.ofNullable(responseBody);
    }

    private void logResponseIfUnexpected(final ResponseEntity<?> responseEntity) {

        HttpStatus expectedResponseStatus = getExpectedHttpStatus();

        if (!expectedResponseStatus.equals(responseEntity.getStatusCode())) {

            log.warn(UNEXPECTED_HTTP_RESPONSE, expectedResponseStatus.value(),
                    expectedResponseStatus.getReasonPhrase(), responseEntity.getStatusCode());
        }
    }

    protected abstract String getServiceName();

    protected abstract ExternalGatewayUrl buildExternalGatewayUrl(CT requestType);

    protected abstract HttpEntity<CT> buildHttpEntity(final CT requestType) throws JsonProcessingException;

    protected abstract HttpStatus getExpectedHttpStatus();

    protected abstract void performCustomErrorChecking(final CT requestType, final RT responseType);
}
