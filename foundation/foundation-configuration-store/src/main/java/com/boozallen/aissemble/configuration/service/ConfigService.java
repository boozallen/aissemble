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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

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
        Property property = null;
        try {
             property = this.configLoader.read(groupName, propertyName);
        } catch (Exception e) {
            logger.error(String.format("Error reading property - groupName: %s, propertyName: %s", groupName, propertyName), e);
        }
        if (property != null) {
            return Response.ok(property.toJsonString()).build();
        }
        return Response.status(404).build();
    }

    @POST
    @Path(("/load"))
    public Response loadProperties() {
        String baseUri = System.getenv("BASE_URI");
        String environmentUri = System.getenv("ENVIRONMENT_URI");
        try {
            Set<Property> properties;
            if (environmentUri != null && !environmentUri.isEmpty()) {
                properties = configLoader.loadConfigs(baseUri,environmentUri);
            } else if (baseUri != null && !baseUri.isEmpty()){
                properties = configLoader.loadConfigs(baseUri);
            } else {
                throw new RuntimeException("Undefined environment variables: BASE_URI and ENVIRONMENT_URI");
            }
            configLoader.write(properties);
            return Response.ok().status(Response.Status.CREATED).build();
        } catch (Exception e) {
            logger.error("Error loading properties:", e);
        }
        return Response.serverError().build();
    }
}
