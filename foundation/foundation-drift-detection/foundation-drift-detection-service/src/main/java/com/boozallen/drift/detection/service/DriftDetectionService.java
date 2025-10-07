package com.boozallen.drift.detection.service;

/*-
 * #%L
 * Drift Detection::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
