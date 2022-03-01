package com.amer.restclient.infrastructure.rest.test.request;

import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestDto {

    @NotBlank
    @JsonProperty("page")
    private final String page;
}
