package com.hyrax.microservice.email.rest.api.controller;

import com.hyrax.client.email.api.request.LabelEventSubscriptionRequest;
import com.hyrax.client.email.api.response.LabelEventSubscriptionResponse;
import com.hyrax.microservice.email.rest.api.exception.ResourceNotFoundException;
import com.hyrax.microservice.email.rest.api.security.AuthenticationUserDetailsHelper;
import com.hyrax.microservice.email.service.api.LabelEventSubscriptionService;
import com.hyrax.microservice.email.service.api.model.LabelEventSubscription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/email/settings/label")
@Api(description = "Operations about label event subscriptions")
@AllArgsConstructor
public class LabelEventSubscriptionRESTController extends AbstractRESTController {

    private static final String ERROR_MESSAGE = "Label event subscription settings are not found";

    private final AuthenticationUserDetailsHelper authenticationUserDetailsHelper;

    private final LabelEventSubscriptionService labelEventSubscriptionService;

    private final ConversionService conversionService;

    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Resource to list the label event subscription settings for the given user")
    public ResponseEntity<LabelEventSubscriptionResponse> retrieveLabelEventSubscriptionSettings() {
        final String username = authenticationUserDetailsHelper.getUsername();
        final LabelEventSubscription labelEventSubscription = labelEventSubscriptionService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ERROR_MESSAGE));
        return ResponseEntity.ok(conversionService.convert(labelEventSubscription, LabelEventSubscriptionResponse.class));
    }

    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "Resource to modify the label event subscription settings for the given user")
    public ResponseEntity<Void> saveOrUpdateLabelEventSubscriptionSettings(@RequestBody final LabelEventSubscriptionRequest labelEventSubscriptionRequest) {
        logger.info("Received label event subscription settings to update : {}", labelEventSubscriptionRequest);
        labelEventSubscriptionService.saveOrUpdate(modelMapper.map(labelEventSubscriptionRequest, LabelEventSubscription.class));
        return ResponseEntity.noContent().build();
    }

}
