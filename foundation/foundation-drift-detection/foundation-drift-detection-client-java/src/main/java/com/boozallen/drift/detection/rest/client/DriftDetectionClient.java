package com.boozallen.drift.detection.rest.client;

/*-
 * #%L
 * Drift Detection::Rest Client
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftDataInput;

@Path("/invoke-drift")
@RegisterRestClient
public interface DriftDetectionClient {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DriftDetectionResult invoke(@QueryParam("policyIdentifier") String policyIdentifier,
            DriftDataInput driftDataInput);

}
