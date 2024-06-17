package com.boozallen.aissemble.configuration.dao;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Configuration::Store::Vault
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.krausening.Krausening;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.vault.VaultContainer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class VaultPropertyDaoSteps {

    private static final Logger logger = LoggerFactory.getLogger(VaultPropertyDaoSteps.class);
    private final VaultPropertyDao propertyDao = new VaultPropertyDao();
    private ConfigLoader configLoader = new ConfigLoader();
    private VaultContainer<?> vaultContainer;
    private Set<Property> properties;
    private String baseURI;
    private String environmentURI;

    @Before("@vault")
    public void setup() {
        setupVaultContainer();
    }

    @After("@vault")
    public void cleanup() {
        vaultContainer = null;
        properties = null;
    }

    @Before("@config-loader")
    public void configLoaderSetup() {
        configLoader = new ConfigLoader();
        configLoader.setPropertyDao(propertyDao);
        properties = null;
    }
    
    @After("@config-loader")
    public void configLoaderCleanup() {
        baseURI = null;
        environmentURI = null;
        configLoader = null;
    }

    @Given("set of properties to be loaded")
    public void configLoaderReadsThePropertiesAtURIs() {
        baseURI = "src/test/resources/configurations/base";
        environmentURI = "src/test/resources/configurations/example-env";
        properties = configLoader.loadConfigs(baseURI, environmentURI);
    }

    @When("the configurations are loaded")
    public void theConfigurationsAreLoaded() {
        configLoader.write(properties);
    }

    @And("the properties can be read from the vault server")
    public void theConfigLoaderUsesVaultPropertyDaoToWriteToVault() {
        for (Property property : properties) {
            assertEquals(propertyDao.read(property.getPropertyKey()), property);
        }
    }

    private void setupVaultContainer() {
        final Properties encryptProperties = Krausening.getInstance().getProperties("encrypt.properties");
        String dockerImage = "ghcr.io/boozallen/aissemble-vault:" + System.getProperty("version.aissemble.vault") + this.getSystemArchitectureTag();
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

    private String getSystemArchitectureTag() {
        String arch = System.getProperty("os.arch").toLowerCase().trim();
        switch(arch) {
            case "aarch64":
            case "arm64":   return "-arm64";
            default:        return "-amd64";
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            logger.error("Failed to read key or token file from aissemble-vault container. Error while converting inputSteam to a String.", e);
        }
        return "";
    }

}
