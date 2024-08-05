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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.krausening.Krausening;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;

import com.boozallen.aissemble.configuration.ConfigStoreInit;
import com.boozallen.aissemble.configuration.dao.ConfigStoreDaoClass;
import com.boozallen.aissemble.configuration.dao.PropertyDao;
import com.boozallen.aissemble.configuration.policy.PropertyRegenerationPolicy;
import com.boozallen.aissemble.configuration.policy.exception.PropertyRegenerationPolicyException;
import com.boozallen.aissemble.util.TestPropertyDao;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;


public class LoadConfigurationsSteps {

    private static final Logger logger = LoggerFactory.getLogger(LoadConfigurationsSteps.class);
    private final Property expectedProperty = new Property("microprofile-config-messaging", "topic", "messaging-topic");
    private final Property expectedDecryptedProperty = new Property("aws-credentials", "AWS_SECRET_ACCESS_KEY", "env-secret-access-key");
    private final Property fullyLoadProperty = new Property("load-status", "fully-loaded", "true");
    private Set<Property> properties;
    private PropertyDao propertyDao;
    private Set<PropertyRegenerationPolicy> policies;
    private ConfigLoader configLoader;
    private static final String basePropertyUri = "./target/test-classes/configurations/base";
    private static final String environmentPropertyURI = "./target/test-classes/configurations/example-env";
    private String basePolicyUri;
    private String environmentPolicyURI;
    private Exception foundError;
    private ValidatableResponse response;
    private VaultContainer<?> vaultContainer;

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

    @Before("@vault")
    public void setupVault() {
        setupVaultContainer();
    }

    @After("@vault")
    public void cleanupVault() {
        vaultContainer = null;
        properties = null;
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
            properties = configLoader.loadConfigs(basePropertyUri, environmentPropertyURI);
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
        assertEquals(13, TestPropertyDao.loadedProperties.size());
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

    @Given("the config loader to configured to use {string}")
    public void theConfigLoaderToConfiguredToUse(String propertyDao) {
        configLoader = CDI.current().select(ConfigLoader.class, new Any.Literal()).get();
        configLoader.setPropertyDaoClass(ConfigStoreDaoClass.valueOf(propertyDao).getValue());
        this.propertyDao = configLoader.getPropertyDao();
    }

    @When("the configurations are written")
    public void theConfigurationsAreWritten() {
        configLoader.write(properties);
    }

    @Then("the properties can be read")
    public void thePropertiesCanBeRead() {
        for (Property property : properties) {
            assertEquals(propertyDao.read(property.getPropertyKey()), property);
        }
    }

    private void setupVaultContainer() {
        final Properties encryptProperties = Krausening.getInstance().getProperties("encrypt.properties");
        String dockerImage = "ghcr.io/boozallen/aissemble-vault:" + System.getProperty("version.aissemble.vault");
        final DockerImageName vaultImage = DockerImageName.parse(dockerImage).asCompatibleSubstituteFor("vault");
        vaultContainer = new VaultContainer(vaultImage);
        vaultContainer.setWaitStrategy(Wait.forListeningPort());
        vaultContainer.start();

        // We override the secrets.host.url property in order to bring the port in-sync with the testcontainers port.
        encryptProperties.setProperty("secrets.host.url", vaultContainer.getHttpHostAddress());

        // Override secrets.root.key values from the aissemble-vault image for VaultPropertyDao vault access.
        String rootKey = vaultContainer.copyFileFromContainer("/root_key.txt", this::inputStreamToString);
        encryptProperties.setProperty("secrets.root.key", rootKey);

        // Override secrets.root.key values from the aissemble-vault image for VaultPropertyDao vault access.
        String unsealKeysJSONArrayString = vaultContainer.copyFileFromContainer("/unseal_keys.txt", this::inputStreamToString);
        encryptProperties.setProperty("secrets.unseal.keys", unsealKeysJSONArrayString.replaceAll("[\\s|\\[\\]\"]", ""));
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            logger.error("Failed to read key or token file from aissemble-vault container. Error while converting inputSteam to a String.", e);
        }
        return "";
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
        expectedProperties.add(new Property("encrypt", "secrets.unseal.keys", "<unseal.keys>"));
        expectedProperties.add(new Property("encrypt", "secrets.root.key", "<root.key>"));
        expectedProperties.add(new Property("encrypt", "secrets.host.url", "http://127.0.0.1:8200"));
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
