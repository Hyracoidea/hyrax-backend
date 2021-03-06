package com.hyrax.microservice.account.rest.api.validation.annotation;

import com.hyrax.microservice.account.rest.api.validation.annotation.validator.LastNameConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LastNameConstraintValidator.class)
public @interface LastName {

    int minLength() default 2;

    int maxLength() default 100;

    String message() default "Last name should contain at least %d and at most %d characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}