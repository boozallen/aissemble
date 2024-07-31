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

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.boozallen.aissemble.configuration.ConfigStoreInit;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;
import com.boozallen.aissemble.configuration.policy.exception.PropertyRegenerationPolicyException;
import com.boozallen.aissemble.util.TestPropertyDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;


public class LoadConfigurationsSteps {

    private final Property expectedProperty = new Property("microprofile-config-messaging", "topic", "messaging-topic");
    private final Property expectedDecryptedProperty = new Property("aws-credentials", "AWS_SECRET_ACCESS_KEY", "env-secret-access-key");
    private final Property fullyLoadProperty = new Property("load-status", "fully-loaded", "true");
    private Set<PropertyRegenerationPolicy> policies;
    private ConfigLoader configLoader;
    private static final String basePropertyUri = "src/test/resources/configurations/base";
    private static final String environmentPropertyURI = "src/test/resources/configurations/example-env";
    private String basePolicyUri;
    private String environmentPolicyURI;
    private Exception foundError;
    private ValidatableResponse response;
    private AdmissionReview admissionReviewRequest;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before("@config-loader")
    public void setup() {
        configLoader = new ConfigLoader();
    }

    @After("@config-loader")
    public void cleanup() {
        foundError = null;
        response = null;
        policies = null;
    }

    @Given("URIs pointing to valid base and environment properties")
    public void URIsPointingToValidBaseAndEnvironmentProperties() {
        // Set in statics
    }

    @Given("URIs pointing to valid base and environment policies")
    public void URIsPointingToValidBaseAndEnvironmentPolicies() {
        basePolicyUri = "src/test/resources/policies/base";
        environmentPolicyURI = "src/test/resources/policies/example-env";
    }

    @Given("there exists a krausening_password and encrypted properties")
    public void thereExistsAKrausening_passwordAndEncryptedProperties() {
        //Values set in test properties files
    }
    
    @When("the configuration service starts")
    public void theConfigurationServiceStarts() {
        // the service is triggered automatically
    }

    @Then("the configurations are loaded into the configuration store")
    public void theConfigurationsAreLoadedIntoConfigStore() {
        String responseBody = getResponseBodyForProperty(expectedProperty.getGroupName(), expectedProperty.getPropertyName());
        assertEquals(expectedProperty.toJsonString(), responseBody);
    }

    @Then("the user is notified that the configurations were loaded successfully")
    public void notifyOnSuccessConfigurationLoad() {
        assertEquals("Expected status to be equal to 'Load Complete'", "Load Complete", ConfigStoreInit.getStatus());
    }

    @Then("the configuration service records the that the given configurations were loaded successfully")
    public void successStatusIsRecorded() {
        String responseBody = getResponseBodyForProperty(fullyLoadProperty.getGroupName(), fullyLoadProperty.getPropertyName());
        assertEquals(fullyLoadProperty.toJsonString(), responseBody);
    }

    @When("the configuration service starts again")
    public void theConfigurationServiceStartsAgain() {
        new ConfigStoreInit().init();;
    }

    @Then("the configuration service skips the loading process")
    public void theConfigurationServiceSkipLoading() {
        // nothing to verify here, handled by assertion below
    }

    @Then("notifies the user that the configurations were previously loaded")
    public void notifyOnPreviousConfigurationFullLoad() {
        assertEquals("Expected status to be equal to 'Skip Load'", "Load Skipped", ConfigStoreInit.getStatus());
    }

    @When("the properties are loaded")
    public void thePropertiesAreLoaded() {
        try {
            configLoader.loadConfigs(basePropertyUri, environmentPropertyURI);
        } catch (Exception error) {
            foundError = error;
        }
    }

    @Then("the ConfigLoader validates the URI and its contents")
    public void theConfigLoaderValidatesTheURIAndItsContents() {
        assertNull(foundError);
    }

    @Then("consumes the base properties")
    public void consumesTheBaseProperties() {
        // verification of consumption occurs after properties are reconciled
    }
 
    @Then("augments the base with the environment properties")
    public void augmentsTheBaseWithTheEnvironmentConfigurations() {
        assertEquals(10, TestPropertyDao.loadedProperties.size());
        assertPropertySetsEqual(createExpectedProperties(), new HashSet<>(TestPropertyDao.loadedProperties.values()));
    }


    @Given("the configuration service has started")
    public void theConfigurationServiceHasStarted() {
        // service started
    }

    @When("requests a configuration property")
    public void requestAConfigurationProperty() {
        String requestGroupName = expectedProperty.getGroupName();
        String requestPropName = expectedProperty.getPropertyName();
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

    @Given("a URI pointing to a policy with a undefined {string}")
    public void URIPointingToPolicyUndefinedAttribute(String attribute) {
        basePolicyUri = "src/test/resources/policies-undefined/" + attribute;
        environmentPolicyURI = null;
    }

    @Given("URIs pointing to policies targeting the same property")
    public void URIsPointingToPoliciesTargetingSameProperty() {
        basePolicyUri = "src/test/resources/policies-same-target";
        environmentPolicyURI = null;
    }

    @When("the policies are loaded")
    public void thePoliciesAreLoaded() {
        try {
            if (environmentPolicyURI != null) {
                this.policies = configLoader.loadPolicies(basePolicyUri, environmentPolicyURI);
            } else {
                this.policies = configLoader.loadPolicies(basePolicyUri);
            }
            
        } catch (Exception error) {
            foundError = error;
        }
    }

    @Then("the ConfigLoader consumes the base and environment policies")
    public void theConfigLoaderConsumesTheBaseAndEnvironmentPolicies() {
        assertNull(foundError);
    }

    @Then("the environment policy overrides the base policy")
    public void theEnvironmentPolicyOverridesTheBasePolicy() {
        assertEquals(3, this.policies.size());

        //verify policy 1 and policy 2 are the environment version
        long envPolicies = policies.stream()
                .filter(policy -> policy.getIdentifier().contains("1") || policy.getIdentifier().contains("2"))
                .filter(policy -> policy.getDescription().contains("environment"))
                .count();

        assertEquals("There should only be 2 environment policies", 2, envPolicies);
    }

    @Then("an exception is thrown stating a policy attribute is undefined")
    public void anExceptionIsThrownStatingPolicyAttributeUndefined() {
        assertEquals("Expected error to be of type 'PropertyRegenerationPolicyException'",  PropertyRegenerationPolicyException.class, foundError.getClass());
        assertTrue(foundError.getMessage().contains("is a required field for a Property Regeneration Policy"));
    }

    @Then("an exception is thrown stating a property cannot be targeted by multiple policies")
    public void anExceptionIsThrownStatingPropertyCannotBeTargetedMultiplePolicies() {
        assertEquals("Expected error to be of type 'PropertyRegenerationPolicyException'",  PropertyRegenerationPolicyException.class, foundError.getClass());
        assertTrue(foundError.getMessage().contains("There should be at most one policy per target property"));
    }

    @Then("the loaded properties contains the decrypted value")
    public void theLoadedPropertiesContainsTheDecryptedValue() {
        String responseBody = getResponseBodyForProperty(expectedDecryptedProperty.getGroupName(),
                expectedDecryptedProperty.getPropertyName());
        assertEquals(expectedDecryptedProperty.toJsonString(), responseBody);
    }

    public Set<Property> createExpectedProperties() {
        Set<Property> expectedProperties = new HashSet<>();

        expectedProperties.add(new Property("aws-credentials", "AWS_ACCESS_KEY_ID", "env-access-key-id"));
        expectedProperties.add(new Property("aws-credentials", "AWS_SECRET_ACCESS_KEY", "env-secret-access-key"));
        expectedProperties.add(new Property("microprofile-config-data-lineage", "connector", "smallrye-kafka"));
        expectedProperties.add(new Property("microprofile-config-data-lineage", "serializer", "apache.StringSerializer"));
        expectedProperties.add(new Property("microprofile-config-data-lineage", "topic", "lineage-topic"));
        expectedProperties.add(new Property("microprofile-config-data-lineage", "cloud-events", "true"));
        expectedProperties.add(new Property("microprofile-config-data-lineage", "added-property", "example-value"));
        expectedProperties.add(new Property("microprofile-config-messaging", "connector", "smallrye-kafka"));
        expectedProperties.add(new Property("microprofile-config-messaging", "serializer", "apache.StringSerializer"));
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
