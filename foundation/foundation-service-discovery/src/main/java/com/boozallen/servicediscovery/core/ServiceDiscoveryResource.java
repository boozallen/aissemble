package com.boozallen.servicediscovery.core;

/*-
 * #%L
 * Service Discovery::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.mutiny.Uni;
import io.vertx.servicediscovery.Record;

/**
 * Resource for service discovery REST calls.
 */
@Path("/service-discovery")
public class ServiceDiscoveryResource {

    @Inject
    ServiceDiscoveryProvider provider;

    /**
     * Endpoint to register a service in Service Discovery.
     * 
     * @param service
     *            the service to register
     * @return the registered service record if registration was successful
     */
    @POST
    @Path("/registry")
    public Uni<Record> register(ServiceRegistration service) {
        return provider.registerService(service);
    }

    /**
     * Endpoint to update services
     *
     * @param registrationId
     *            the ID of the service to update
     * @param record
     *            the record with the values to use for the service
     * @return the list of services with the given type
     */
    @PUT
    @Path("/registry/{registrationId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Boolean> update(@PathParam("registrationId") String registrationId, final Record record) {
        record.setRegistration(registrationId);
        return provider.update(record);
    }

    /**
     * Endpoint to unregister a service in Service Discovery.
     * 
     * @param registrationId
     *            the registration id of the service to unregister
     * @return whether the unregistration was successful or not
     */
    @DELETE
    @Path("/registry/{registrationId}")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<Boolean> unregister(@PathParam("registrationId") String registrationId) {
        return provider.unregisterService(registrationId);
    }

    /**
     * Endpoint to retrieve registered services in Service Discovery with the
     * given name.
     * 
     * @param name
     *            the name of the services to retrieve
     * @return the list of services with the given name
     */
    @GET
    @Path("/service/{name}")
    public Uni<List<Record>> getByName(@PathParam("name") String name,
                                       @QueryParam("includeOutOfService") Boolean includeOutOfService) {
        Uni<List<Record>> results;
        if (includeOutOfService != null) {
            results = provider.getServicesByName(name, includeOutOfService);
        } else {
            results = provider.getServicesByName(name);
        }

        return results;
    }

    /**
     * Endpoint to retrieve registered services in Service Discovery with the
     * given type.
     *
     * @param type
     *            the type of the services to retrieve
     * @return the list of services with the given type
     */
    @GET
    @Path("/service")
    public Uni<List<Record>> getByType(@QueryParam("type") String type,
                                       @QueryParam("includeOutOfService") Boolean includeOutOfService) {
        Uni<List<Record>> results;
        if (includeOutOfService != null) {
            results = provider.getServiceByType(type, includeOutOfService);
        } else {
            results = provider.getServiceByType(type);
        }

        return results;
    }

}
