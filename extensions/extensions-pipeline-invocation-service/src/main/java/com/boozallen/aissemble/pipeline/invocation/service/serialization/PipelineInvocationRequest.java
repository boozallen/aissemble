package com.boozallen.aissemble.pipeline.invocation.service.serialization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.pipeline.invocation.service.ExecutionProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Deserialization class representing the received parameters for a pipeline invocation request
 */
public class PipelineInvocationRequest {

    private static final Logger logger = LoggerFactory.getLogger(PipelineInvocationRequest.class);

    /**
     * Creates a PipelineInvocationRequest object from a given JSON String
     *
     * @param payload JSON formatted string to be deserialized
     * @return Object representation of the received JSON
     * @throws JsonProcessingException in the event of a non-JSON format string being received
     */
    public static PipelineInvocationRequest fromString(String payload) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(payload, PipelineInvocationRequest.class);
    }

    private Map<String, String> overrideValues = new HashMap<>();

    private String applicationName;

    private String profile;

    /**
     * @return Individual value overrides to be applied atop the composed chart
     */
    public Map<String, String> getOverrideValues() {
        return overrideValues;
    }
    public void setOverrideValues(Map<String, String> overrideValues) {
        this.overrideValues = overrideValues;
    }

    /**
     * Determines which profile to use.  If the originating request specifies a profile, that profile will be used.
     * Otherwise, defer to the default specified in the configuration.
     * @return The base execution profile to use
     */
    public ExecutionProfile getProfile() {
        // This is a non-bean class, so we have to access the config using the programmatic api
        Optional<String> optionalDefaultProfile =
                ConfigProvider.getConfig().getOptionalValue("service.pipelineInvocation.execution.profile", String.class);
        String targetProfile = (this.profile != null) ? this.profile : optionalDefaultProfile.orElse("dev");
        return ExecutionProfile.valueOf(targetProfile.toUpperCase());
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @return The requested application name
     */
    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
