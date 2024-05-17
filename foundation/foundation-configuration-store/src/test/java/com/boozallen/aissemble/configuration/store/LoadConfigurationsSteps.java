package com.boozallen.aissemble.configuration.store;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import io.restassured.response.ValidatableResponse;
import com.boozallen.aissemble.util.TestPropertyDao;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.*;

public class LoadConfigurationsSteps {

    private final Property expectedProperty = new Property("messaging", "topic", "messaging-topic");
    private ConfigLoader configLoader;
    private String baseURI;
    private String environmentURI;
    private Exception foundError;
    private ValidatableResponse response;


    @Before("@config-loader")
    public void setup() {
        configLoader = new ConfigLoader();
    }

    @After("@config-loader")
    public void cleanup() {
        foundError = null;
        response = null;
    }

    @Given("URIs pointing to valid base and environment configurations")
    public void URIsPointingToValidBaseAndEnvironmentConfigurations() {
        baseURI = "src/test/resources/configurations/base";
        environmentURI = "src/test/resources/configurations/example-env";
    }

    @When("the configurations are loaded")
    public void theConfigurationsAreLoaded() {
        try {
            Set<Property> result = configLoader.loadConfigs(baseURI, environmentURI);
        } catch (Exception error) {
            foundError = error;
        }
    }

    @Then("the ConfigLoader validates the URI and its contents")
    public void theConfigLoaderValidatesTheURIAndItsContents() {
        assertNull(foundError);
    }

    @And("consumes the base configurations")
    public void consumesTheBaseConfigurations() {
        // verification of consumption occurs after configurations are reconciled
    }

    @And("augments the base with the environment configurations")
    public void augmentsTheBaseWithTheEnvironmentConfigurations() {
        assertEquals(10, TestPropertyDao.loadedProperties.size());
        assertPropertySetsEqual(createExpectedProperties(), new HashSet<>(TestPropertyDao.loadedProperties.values()));
    }

    @Given("URIs pointing to misformatted configurations")
    public void URIsPointingToMisformattedConfigurations() {
        baseURI = "src/test/resources/configurations-misformatted/base";
        environmentURI = "src/test/resources/configurations-misformatted/example-env";
    }

    @Then("an exception is thrown stating configurations are misformatted")
    public void anExceptionIsThrownStatingConfigurationsAreMisformatted() {
        String expectedMessage = "Could not parse yaml";
        assertEquals(IllegalArgumentException.class, foundError.getClass());
        assertEquals(expectedMessage, foundError.getMessage());
    }

    @Given("URIs pointing to nondistinct configurations")
    public void URIsPointingToNondistinctConfigurations() {
        baseURI = "src/test/resources/configurations-nondistinct/base";
        environmentURI = "src/test/resources/configurations-nondistinct/example-env";
    }

    @Then("an exception is thrown stating configurations are not distinguishable")
    public void anExceptionIsThrownStatingConfigurationsAreNotDistinguishable() {
        String expectedMessage = "Duplicates found";
        assertEquals(IllegalArgumentException.class, foundError.getClass());
        assertEquals(expectedMessage, foundError.getMessage());
    }

    @When("the configuration service starts")
    public void theConfigurationServiceStarts() {
        // service started
    }

    @Given("the configuration service has started")
    public void theConfigurationServiceHasStarted() {
        // service started
    }

    @When("requests a configuration property")
    public void requestAConfigurationProperty() {
        String requestGroupName = expectedProperty.getGroupName();
        String requestPropName = expectedProperty.getName();
        response = given()
                .pathParam("groupName", requestGroupName)
                .pathParam("propertyName", requestPropName)
                .when().get("/aissemble-properties/{groupName}/{propertyName}")
                .then();
    }

    @Then("the property value is returned")
    public void thePropertyValueIsReturned() {
        response.statusCode(200)
                .body(is(expectedProperty.toJsonString()));
    }

    public Set<Property> createExpectedProperties() {
        Set<Property> expectedProperties = new HashSet<>();

        expectedProperties.add(new Property("model-training-api", "AWS_ACCESS_KEY_ID", "env-access-key-id"));
        expectedProperties.add(new Property("model-training-api", "AWS_SECRET_ACCESS_KEY", "env-secret-access-key"));
        expectedProperties.add(new Property("data-lineage", "connector", "smallrye-kafka"));
        expectedProperties.add(new Property("data-lineage", "serializer", "apache.StringSerializer"));
        expectedProperties.add(new Property("data-lineage", "topic", "lineage-topic"));
        expectedProperties.add(new Property("data-lineage", "cloud-events", "true"));
        expectedProperties.add(new Property("data-lineage", "added-property", "example-value"));
        expectedProperties.add(new Property("messaging", "connector", "smallrye-kafka"));
        expectedProperties.add(new Property("messaging", "serializer", "apache.StringSerializer"));
        expectedProperties.add(expectedProperty);

        return expectedProperties;
    }

    private void assertPropertySetsEqual(Set<Property> expected, Set<Property> result) {
        for (Property expectedProperty : expected) {
            boolean matchFound = false;
            for (Property property : result) {
                if (expectedProperty.equals(property) && expectedProperty.getValue().equals(property.getValue())) {
                    matchFound = true;
                    break;
                }
            }
            assertTrue("Could not find " + expectedProperty, matchFound);
        }
    }
}
