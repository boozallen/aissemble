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

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.vertx.servicediscovery.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@SuppressWarnings("unchecked")
public class ServiceDiscoveryResourceTest {

    @Test
    public void testRegister() {
        ServiceRegistration service = createTestService();

        // create register request for service
        RequestSpecification registerRequest = given()
                .body(service)
                .contentType(MediaType.APPLICATION_JSON);

        // send the register request
        Response registerResponse = registerRequest
                .when()
                .post("/service-discovery/registry");

        // verify expected response from register request
        registerResponse.then()
                .statusCode(200)
                .body("registration", notNullValue())
                .body("status", notNullValue())
                .body("name", is(service.getName()))
                .body("type", is(service.getType()))
                .body("location.map.endpoint", is(service.getEndpoint()))
                .body("metadata.map", is(service.getMetadata()));
    }

    @Test
    public void testUnregister_serviceExists() {
        ServiceRegistration service = createTestService();

        // register the service
        Response registerResponse = given()
                .body(service)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/service-discovery/registry");

        // get the registration id of the registered service
        String registrationId = registerResponse
                .jsonPath().getString("registration");

        // send request to unregister the service
        Response unregisterResponse = when()
                .delete("/service-discovery/registry/{registrationId}", registrationId);

        // verify expected response from unregister request
        unregisterResponse.then()
                .statusCode(200)
                .body(is(Boolean.TRUE.toString()));
    }

    @Test
    public void testUnregister_serviceDoesNotExist() {
        // send request to unregister service that doesn't exist
        Response unregisterResponse = when()
                .delete("/service-discovery/registry/{registrationId}", RandomStringUtils.randomAlphanumeric(5));

        // verify expected response from unregister request
        unregisterResponse.then()
                .statusCode(200)
                .body(is(Boolean.FALSE.toString()));
    }

    @Test
    public void testGetByName() {
        ServiceRegistration service = createTestService();

        // register the service
        Response registerResponse = given()
                .body(service)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/service-discovery/registry");

        // send request to get services by name
        Response getResponse = when()
                .get("service-discovery/service/{name}", service.getName());

        // verify expected response and extract the returned list
        List<Map<String, Object>> results = getResponse.then()
                .statusCode(200)
                .extract().as(List.class);

        // verify expected results from the get request
        assertEquals(1, results.size(), "Unexpected number of services returned");

        Object expectedRegistrationId = registerResponse.jsonPath().get("registration");
        Object actualRegistrationId = results.get(0).get("registration");
        assertEquals(expectedRegistrationId, actualRegistrationId, "Unexpected service returned");
    }

    @Test
    public void testGetByType() {
        ServiceRegistration service = createTestService();

        // register the service
        Response registerResponse = given()
                .body(service)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/service-discovery/registry");

        // send request to get services by name
        Response getResponse = given()
                .queryParam("type", service.getType())
                .when()
                .get("service-discovery/service");

        // verify expected response and extract the returned list
        List<Map<String, Object>> results = getResponse.then()
                .statusCode(200)
                .extract()
                .as(List.class);

        // verify expected results from the get request
        assertEquals(1, results.size(), "Unexpected number of services returned");

        Object expectedRegistrationId = registerResponse.jsonPath().get("registration");
        Object actualRegistrationId = results.get(0).get("registration");
        assertEquals(expectedRegistrationId, actualRegistrationId, "Unexpected service returned");
    }

    @Test
    public void testUpdate() {
        final ServiceRegistration service = createTestService();

        final Response registerResponse = given()
                .body(service)
                .contentType(MediaType.APPLICATION_JSON)
                .post("/service-discovery/registry");

        final Response getResponse = when()
                .get("service-discovery/service/{name}", service.getName());

        // verify expected response and extract the returned list
        List<Map<String, Object>> results = (List<Map<String, Object>>) getResponse.then()
                .statusCode(200)
                .extract()
                .as(List.class);

        Map<String, Object> record = results.get(0);
        record.put("status", Status.DOWN.toString());

        final Response updateResponse = given()
                .body(record)
                .contentType(MediaType.APPLICATION_JSON)
                .put("/service-discovery/registry/{registrationId}", record.get("registration"));

        updateResponse.then()
                .statusCode(200)
                .body(is(Boolean.TRUE.toString()));

        final Response getResult = given()
                .queryParam("includeOutOfService", true)
                .when()
                .get("service-discovery/service/{name}", service.getName());

        // verify expected response and extract the returned list
        List<Map<String, Object>> updatedResults = (List<Map<String, Object>>) getResult.then()
                .statusCode(200)
                .extract()
                .as(List.class);

        Map<String, Object> updatedRecord = updatedResults.get(0);
        assertEquals(Status.DOWN.toString(), updatedRecord.get("status"), "Updated record does not have a down status");
    }

    private ServiceRegistration createTestService() {
        ServiceRegistration service = new ServiceRegistration();
        service.setName(RandomStringUtils.randomAlphanumeric(5));
        service.setType(RandomStringUtils.randomAlphanumeric(5));
        service.setEndpoint(RandomStringUtils.randomAlphanumeric(5));
        service.setMetadata(new HashMap<>());
        for (int i = 0; i < 5; i++) {
            service.getMetadata().put(RandomStringUtils.randomAlphanumeric(5), RandomStringUtils.randomAlphanumeric(5));
        }

        return service;
    }

}
