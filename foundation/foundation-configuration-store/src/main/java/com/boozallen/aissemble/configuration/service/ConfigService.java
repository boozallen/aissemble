package com.boozallen.aissemble.configuration.service;
/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/aissemble-properties")
public class ConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Inject
    public ConfigLoader configLoader;

    @GET
    @Path("/{groupName}/{propertyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperty(@PathParam("groupName") String groupName,
                              @PathParam("propertyName") String propertyName) {
        PropertyKey propertyKey = new PropertyKey(groupName, propertyName);
        Property property = null;
        try {
             property = this.configLoader.read(propertyKey);
        } catch (Exception e) {
            logger.error(String.format("Error reading property - groupName: %s, propertyName: %s", groupName, propertyName), e);
        }
        if (property != null) {
            return Response.ok(property.toJsonString()).build();
        }
        return Response.status(404).build();
    }

    @GET
    @Path("/healthcheck")
    @Produces({MediaType.TEXT_PLAIN})
    public String healthCheck() {
        return "Configuration Store service is running...\n";
    }
}
