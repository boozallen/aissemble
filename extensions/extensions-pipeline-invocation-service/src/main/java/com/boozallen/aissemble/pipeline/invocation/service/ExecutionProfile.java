package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Specification of execution profiles, representing layering schemes for values files
 */
public enum ExecutionProfile {
    DEV("base", "dev"),
    CI("base", "dev", "ci"),
    PROD("base", "dev", "ci", "prod");

    private final List<String> layers;

    /**
     * Private enum constructor
     * @param classifiers Ordered stream of zero to many [0,*) value classifiers to be included in this execution profile
     */
    ExecutionProfile(String... classifiers) {
        this.layers = Arrays.stream(classifiers).collect(Collectors.toList());
    }

    /**
     * @return Layering scheme for a given profile
     */
    public List<String> getLayers() {
        return List.copyOf(layers);
    }
}
