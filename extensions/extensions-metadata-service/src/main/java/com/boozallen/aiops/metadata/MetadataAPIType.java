package com.boozallen.aiops.metadata;

/*-
 * #%L
 * AIOps Docker Baseline::AIOps Metadata Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

/**
 * MetadataType annotation to enable dynamic CDI injection specification
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {METHOD, FIELD, PARAMETER, TYPE})
public @interface MetadataAPIType {
    String value();
}
