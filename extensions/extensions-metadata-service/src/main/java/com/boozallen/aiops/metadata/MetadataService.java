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

import com.boozallen.aissemble.core.metadata.MetadataAPI;
import com.boozallen.aissemble.core.metadata.MetadataModel;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

@Path("/metadata")
public class MetadataService {

    @Inject
    @MetadataAPIType("hive")
    MetadataAPI metadataAPI;

    /**
     * Gets all metadata
     * @return all saved metadata
     */
    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMetadata() {
        List<MetadataModel> metadata = metadataAPI.getMetadata(new HashMap<>());
        return Response.status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET")
                .entity(metadata)
                .build();
    }

    @GET
    @Path("/healthcheck")
    @Produces({MediaType.TEXT_PLAIN})
    public String healthCheck() {
        return "Metadata service is running...\n";
    }
}
