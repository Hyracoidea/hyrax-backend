package com.hyrax.microservice.project.rest.api.domain.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RequestValidationDetail implements Serializable {

    private static final long serialVersionUID = -531773045991045910L;

    private final String field;

    private final String message;
}
