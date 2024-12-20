package com.boozallen.drift.detection.service;

/*-
 * #%L
 * Drift Detection::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.DriftDetector;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.data.DriftDataInput;

@Path("/invoke-drift")
public class DriftDetectionService {

    private static ObjectMapper mapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
            .build();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DriftDetectionResult invoke(@QueryParam("policyIdentifier") String policyIdentifier,
                                       String driftDataJson) {

        DriftDataInput driftDataInput = null;
        try {
            driftDataInput = mapper.readValue(driftDataJson, DriftDataInput.class);
        } catch (JsonProcessingException ex) {
            DriftDetectionResult result = new DriftDetectionResult();
            result.getMetadata().put("error", ex.getMessage());
            return result;
        }

        DriftData input = null;
        DriftData control = null;

        if (driftDataInput != null) {
            input = driftDataInput.getInput();
            control = driftDataInput.getControl();
        }
        DriftDetector driftDetector = new DriftDetector();

        return driftDetector.detect(policyIdentifier, input, control);
    }

    @GET
    @Path("/healthcheck")
    @Produces({MediaType.TEXT_PLAIN})
    public String healthCheck() {
        return "Drift detection service is running...\n";
    }
}
