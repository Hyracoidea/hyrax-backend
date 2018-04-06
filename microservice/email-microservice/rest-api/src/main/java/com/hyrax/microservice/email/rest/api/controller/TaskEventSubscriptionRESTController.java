package com.hyrax.microservice.email.rest.api.controller;

import com.hyrax.client.email.api.request.TaskEventSubscriptionRequest;
import com.hyrax.client.email.api.response.TaskEventSubscriptionResponse;
import com.hyrax.microservice.email.rest.api.security.AuthenticationUserDetailsHelper;
import com.hyrax.microservice.email.service.api.TaskEventSubscriptionService;
import com.hyrax.microservice.email.service.api.model.TaskEventSubscription;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/email/settings/task")
@Api(description = "Operations about task event subscriptions")
@AllArgsConstructor
public class TaskEventSubscriptionRESTController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskEventSubscriptionRESTController.class);

    private final AuthenticationUserDetailsHelper authenticationUserDetailsHelper;

    private final TaskEventSubscriptionService taskEventSubscriptionService;

    private final ConversionService conversionService;

    private final ModelMapper modelMapper;

    @GetMapping
    @ApiOperation(httpMethod = "GET", value = "Resource to list the task event subscription settings for the given user")
    public ResponseEntity<TaskEventSubscriptionResponse> retrieveTaskEventSubscriptionSettings() {
        final String username = authenticationUserDetailsHelper.getUsername();
        return ResponseEntity.ok(conversionService.convert(taskEventSubscriptionService.findByUsername(username), TaskEventSubscriptionResponse.class));
    }

    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "Resource to modify the task event subscription settings for the given user")
    public ResponseEntity<Void> saveOrUpdateTaskEventSubscriptionSettings(@RequestBody final TaskEventSubscriptionRequest taskEventSubscriptionRequest) {
        LOGGER.info("Received task event subscription settings to update : {}", taskEventSubscriptionRequest);
        taskEventSubscriptionService.saveOrUpdate(modelMapper.map(taskEventSubscriptionRequest, TaskEventSubscription.class));
        return ResponseEntity.noContent().build();
    }

}
