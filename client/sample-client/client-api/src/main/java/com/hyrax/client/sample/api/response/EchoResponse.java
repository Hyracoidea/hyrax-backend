package com.hyrax.client.sample.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EchoResponse {

    private final Long id;
    private final String message;
}
