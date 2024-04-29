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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.mutiny.core.Promise;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

/**
 * Service Discovery provider to handle service registration.
 */
@ApplicationScoped
public class ServiceDiscoveryProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryProvider.class);

    @Inject
    Vertx vertx;

    private ServiceDiscovery discovery;

    /**
     * Creates an instance of the vert.x Service Discovery upon initialization.
     */
    @PostConstruct
    void initialize() {
        final ServiceDiscoveryOptions options = new ServiceDiscoveryOptions();
        options.setAnnounceAddress("service-discovery-announce");
        options.setName("service-discovery-provider");

        discovery = ServiceDiscovery.create(vertx, options);
    }

    /**
     * Closes the vert.x Service Discovery instance upon teardown.
     */
    @PreDestroy
    void cleanup() {
        discovery.close();
    }

    /**
     * Registers the given service in Service Discovery.
     * 
     * @param service
     *            the service to register
     * @return an asynchronous result containing the service registration record
     *         if registration was successful
     */
    public Uni<Record> registerService(final ServiceRegistration service) {
        // promise object to handle asynchronous registration
        final Promise<Record> promise = Promise.promise();
        final Record serviceRecord = service.mapToServiceDiscoveryRecord();

        // register service in service discovery
        discovery.publish(serviceRecord, handler -> {
            if (handler.succeeded()) {
                Record publishedRecord = handler.result();
                logger.info("Successfully registered service '{}': {}", publishedRecord.getName(),
                        publishedRecord.getRegistration());
                // notify that registration is completed successfully
                promise.complete(publishedRecord);
            } else {
                String message = "Failed to register service " + service.getName();
                logger.error(message, handler.cause());
                // notify that registration completed in failure
                promise.fail(message);
            }
        });

        // return future object which will be aware of when the registration
        // completes and will contain the registered service record if
        // successful
        return promise.future();
    }

    /**
     * Unregisters the service in Service Discovery with the given registration
     * id.
     * 
     * @param registrationId
     *            the registration id of the service to unregister
     * @return an asynchronous result indicating whether or not the
     *         unregistration was successful
     */
    public Uni<Boolean> unregisterService(final String registrationId) {
        // promise object to handle asynchronous unregistration
        final Promise<Boolean> promise = Promise.promise();

        // unregister service from service discovery
        discovery.unpublish(registrationId, handler -> {
            if (handler.succeeded()) {
                logger.info("Successfully unregistered service {}", registrationId);
                // notify that unregistration was successful
                promise.complete(true);
            } else {
                logger.error("Failed to unregister service {}", registrationId, handler.cause());
                // notify that unregistration was not successful
                promise.complete(false);
            }
        });

        // return future object that will be aware of when unregistration is complete
        return promise.future();
    }

    /**
     * Returns a list of registered services in Service Discovery with the given
     * name.
     * 
     * @param name
     *            the name of the services to retrieve
     * @return an asynchronous result containing the list of registered services
     *         with the given name
     */
    public Uni<List<Record>> getServicesByName(final String name) {
        return getServicesByName(name, false);
    }

    /**
     * Returns a list of registered services in Service Discovery with the given
     * name.
     *
     * @param name
     *            the name of the services to retrieve
     * @param includeOutOfService
     *            whether or not to include services with out of service statuses
     * @return an asynchronous result containing the list of registered services
     *         with the given name
     */
    public Uni<List<Record>> getServicesByName(final String name, final boolean includeOutOfService) {
        // promise object to handle asynchronous search
        final Promise<List<Record>> promise = Promise.promise();

        // filter registered records by name
        discovery.getRecords(filter -> name.equalsIgnoreCase(filter.getName()), includeOutOfService, handler -> {
            if (handler.succeeded()) {
                List<Record> results = handler.result();
                logger.info("{} services found with name '{}'", results.size(), name);
                // notify that the search completed successfully
                promise.complete(results);
            } else {
                String message = "Failed to retrieve services with name '" + name + "'";
                logger.error(message, handler.cause());
                // notify that the search completed in failure
                promise.fail(message);
            }
        });

        // return future object that will be aware of when the search is
        // complete and will contain the list of matched service records if
        // successful
        return promise.future();
    }

    /**
     * Returns a list of registered services in Service Discovery with the given
     * type.
     *
     * @param type
     *            the type of the services to retrieve
     * @return an asynchronous result containing the list of registered services
     *         with the given type
     */
    public Uni<List<Record>> getServiceByType(final String type) {
        return getServiceByType(type, false);
    }

    /**
     * Returns a list of registered services in Service Discovery with the given
     * type.
     *
     * @param type
     *            the type of the services to retrieve
     * @param includeOutOfService
     *            whether or not to include services with out of service status
     * @return an asynchronous result containing the list of registered services
     *         with the given type
     */
    public Uni<List<Record>> getServiceByType(final String type, final Boolean includeOutOfService) {
        // promise object to handle asynchronous search
        final Promise<List<Record>> promise = Promise.promise();

        if (type != null) {
            // filter registered records by name
            discovery.getRecords(filter -> type.equalsIgnoreCase(filter.getType()), includeOutOfService, handler -> {
                if (handler.succeeded()) {
                    List<Record> results = handler.result();
                    logger.info("{} services found with type '{}'", results.size(), type);
                    // notify that the search completed successfully
                    promise.complete(results);
                } else {
                    String message = "Failed to retrieve services with name {}";
                    logger.error(message, type, handler.cause());
                    // notify that the search completed in failure
                    promise.fail(message);
                }
            });
        } else {
            promise.fail("Must provide a type");
        }

        // return future object that will be aware of when the search is
        // complete and will contain the list of matched service records if
        // successful
        return promise.future();
    }

    /**
     * Update an existing record. Record must be published and have a registration ID
     * @param record the record to update
     * @return true if successfully updated, false otherwise
     */
    public Uni<Boolean> update(final Record record) {
        final Promise<Boolean> promise = Promise.promise();

        if (record != null) {
            discovery.update(record, handler -> {
                if (handler.succeeded()) {
                    logger.info("{} service updated", record.getName());
                    final Record result = handler.result();
                    // notify that the search completed successfully
                    promise.complete(true);
                } else {
                    String message = "Failed to update service with name {}";
                    logger.error(message, record.getName(), handler.cause());
                    // notify that the search completed in failure
                    promise.complete(false);
                }
            });
        } else {
            promise.fail("Record must not be null");
        }

        return promise.future();
    }

}
