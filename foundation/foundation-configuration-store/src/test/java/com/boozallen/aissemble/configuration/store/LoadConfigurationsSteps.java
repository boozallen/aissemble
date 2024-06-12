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
import com.boozallen.aissemble.util.ConfigStoreInitTestHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;


public class LoadConfigurationsSteps {

    private final Property expectedProperty = new Property("messaging", "topic", "messaging-topic");
    private final Property fullyLoadProperty = new Property("load-status", "fully-loaded", "true");
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

    @Given("a base URI indicating a directory housing valid base configurations")
    public void URIsPointingToValidBaseConfigurations() {
        baseURI = "src/test/resources/configurations/base";
    }

    @And("an environment-specific URI indicating a directory housing valid environment-specific configurations")
    public void URIsPointingToValidEnvironmentConfigurations() {
        environmentURI = "src/test/resources/configurations/example-env";
    }
    
    @When("the configuration service starts")
    public void theConfigurationServiceStarts() {
        // the service is triggered automatically
    }

    @Then("the configurations are loaded into the configuration store")
    public void theConfigurationsAreLoadedIntoConfigStore() {
        String responseBody = getResponseBodyForProperty(expectedProperty.getGroupName(), expectedProperty.getName());
        assertEquals(expectedProperty.toJsonString(), responseBody);
    }

    @And("the user is notified that the configurations were loaded successfully")
    public void notifyOnSuccessConfigurationLoad() {
        assertTrue("load successful", ConfigStoreInitTestHelper.verification.get("load successful").equals("true"));
    }

    @And("the configuration service records the that the given configurations were loaded successfully")
    public void successStatusIsRecorded() {
        String responseBody = getResponseBodyForProperty(fullyLoadProperty.getGroupName(), fullyLoadProperty.getName());
        assertEquals(fullyLoadProperty.toJsonString(), responseBody);
    }

    @Given("the configuration store has been fully populated with the specified configurations")
    public void theConfigurationsFullyLoaded(){
        ConfigStoreInitTestHelper.loadProperties();
    }

    @Then("the configuration service skips the loading process")
    public void theConfigurationServiceSkipLoading() {
        assertTrue("skip reloading", ConfigStoreInitTestHelper.verification.get("skip reloading").equals("true"));
    }

    @And("notifies the user that the configurations were previously loaded")
    public void notifyOnPreviousConfigurationFullLoad() {
        assertTrue("skipping reloading notification sent", ConfigStoreInitTestHelper.skiploadingNotificationSent);
    }

    @Given("URIs pointing to misformatted configurations")
    public void URIsPointingToMisformattedConfigurations() {
        baseURI = "src/test/resources/configurations-misformatted/base";
        environmentURI = "src/test/resources/configurations-misformatted/example-env";
    }
    @When("the configurations are loaded")
    public void theConfigurationsAreLoaded() {
        try {
            Set<Property> result = configLoader.loadConfigs(baseURI, environmentURI);
        } catch (Exception error) {
            foundError = error;
        }
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

    private String getResponseBodyForProperty(String requestGroupName, String requestPropName) {        
        ValidatableResponse response = given()
                .pathParam("groupName", requestGroupName)
                .pathParam("propertyName", requestPropName)
                .when().get("/aissemble-properties/{groupName}/{propertyName}")
                .then();
        return response.extract().body().asString();
    }

}