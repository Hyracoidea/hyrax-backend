package com.hyrax.spring.boot.starter.authentication.rest.service.impl;

import com.hyrax.spring.boot.starter.authentication.model.SecuredAccountDetails;
import com.hyrax.spring.boot.starter.authentication.rest.client.SecuredAccountRESTClient;
import com.hyrax.spring.boot.starter.authentication.rest.service.SecuredAccountRESTService;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

public class SecuredAccountRESTServiceImpl implements SecuredAccountRESTService {

    private final SecuredAccountRESTClient securedAccountRESTClient;

    public SecuredAccountRESTServiceImpl(final SecuredAccountRESTClient securedAccountRESTClient) {
        this.securedAccountRESTClient = securedAccountRESTClient;
    }

    @Override
    public Optional<SecuredAccountDetails> retrieveSecuredAccount(final String username) {
        SecuredAccountDetails result = null;
        Response response = null;
        try {
            response = securedAccountRESTClient.callRetrieveSecuredAccountEndpoint(username);
            result = processResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }
        return Optional.ofNullable(result);
    }

    private SecuredAccountDetails processResponse(final Response response) {
        SecuredAccountDetails result = null;

        switch (response.getStatusInfo().getFamily()) {
            case SUCCESSFUL:
                result = response.readEntity(SecuredAccountDetails.class);
                break;
            case CLIENT_ERROR:
                response.readEntity(String.class);
                break;
            case SERVER_ERROR:
                response.readEntity(String.class);
                break;
            default:
                response.readEntity(String.class);
                break;
        }

        return result;
    }
}
