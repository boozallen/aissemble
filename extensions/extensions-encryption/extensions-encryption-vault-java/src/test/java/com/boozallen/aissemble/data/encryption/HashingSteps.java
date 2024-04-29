package com.boozallen.aissemble.data.encryption;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.github.dockerjava.api.model.Capability;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.technologybrewery.krausening.Krausening;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class HashingSteps {
    private String dataElement = "123-456-7890";
    private String dataElementVerbose = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed placerat gravida elementum. Nulla lobortis, erat vel viverra vulputate, elit tortor tristique orci, at accumsan velit massa nec nisl. Fusce consectetur consectetur ultricies. In sit amet dolor nec sapien maximus ornare. Suspendisse potenti. Morbi euismod ornare orci. Duis tempus diam vel eros ornare, ac tempus nisl scelerisque. Sed at ex et nulla porta ultricies vel viverra enim. Fusce id lacinia lectus, eget mattis magna. Sed in risus sodales, lacinia diam a, consectetur nibh.";
    private String encryptedData;
    private AiopsEncrypt simpleAiopsEncrypt = new SimpleAesEncrypt();
    private AiopsEncrypt vaultAiopsEncrypt;

    private static final Logger logger = LoggerFactory.getLogger(HashingSteps.class);
    private final GenericContainer<?> genericContainer = new GenericContainer<>(
            DockerImageName.parse(
                    "ghcr.io/boozallen/aissemble-vault:" + System.getProperty("version.aissemble.vault") + this.getSystemArchitectureTag()
            )
    ).withCreateContainerCmdModifier(
            cmd -> cmd.withCapAdd(Capability.IPC_LOCK)
    );

    private String getSystemArchitectureTag() {
        String arch = System.getProperty("os.arch").toLowerCase().trim();
        switch(arch) {
            case "aarch64":
            case "arm64":   return "-arm64";
            default:        return "-amd64";
        }
    }

    @After("@hashing")
    public void tearDown() {
        encryptedData = null;
    }

    @Before("@vaulthashing")
    public void setup() throws Exception {
        genericContainer.addEnv("VAULT_DEV_ROOT_TOKEN_ID", "myroot");
        genericContainer.addEnv("VAULT_DEV_LISTEN_ADDRESS", "0.0.0.0:8200");
        genericContainer.addExposedPorts(8200);
        genericContainer.setWaitStrategy(Wait.forListeningPort());
        genericContainer.start();
        // We override the secrets.host.url property in order to bring the port in-sync with the testcontainers port.
        Krausening.getInstance().getProperties("encrypt.properties").setProperty(
                "secrets.host.url", "http://127.0.0.1:" + genericContainer.getMappedPort(8200));

        // Override secrets.root.key, secrets.unseal.keys, and encrypt.client.token with values from the aissemble-vault image.
        String rootKey = genericContainer.copyFileFromContainer("/root_key.txt", this::inputStreamToString);
        Krausening.getInstance().getProperties("encrypt.properties").setProperty(
                "secrets.root.key", rootKey);

        String unsealKeysJSONArrayString = genericContainer.copyFileFromContainer("/unseal_keys.txt", this::inputStreamToString);
        try {
            Object unsealKeysObject = new JSONParser().parse(unsealKeysJSONArrayString);
            JSONArray unsealKeysJSONArray = (JSONArray) unsealKeysObject;
            String unsealKeysString = (String) unsealKeysJSONArray.stream().collect(Collectors.joining(","));
            Krausening.getInstance().getProperties("encrypt.properties").setProperty(
                    "secrets.unseal.keys", unsealKeysString);
        } catch (Exception e) {
            logger.error("Error while retrieving unseal keys from aissemble-vault images' unseal_keys.txt. File contents: "
                    + unsealKeysJSONArrayString, e);
        }

        String transitClientTokenJSONString = genericContainer.copyFileFromContainer("/transit_client_token.txt", this::inputStreamToString);
        try {
            Object transitClientTokenObject = new JSONParser().parse(transitClientTokenJSONString);
            JSONObject transitClientTokenJSONObject = (JSONObject) transitClientTokenObject;
            JSONObject transitClientTokenJSONObjectAuth = (JSONObject) transitClientTokenJSONObject.get("auth");
            String clientToken = (String) transitClientTokenJSONObjectAuth.get("client_token");
            Krausening.getInstance().getProperties("encrypt.properties").setProperty(
                    "encrypt.client.token", clientToken);
        } catch (Exception e) {
            logger.error("Error while retrieving encrypt client token from aissemble-vault images' transit_client_token.txt. File contents: "
                    + transitClientTokenJSONString, e);
        }

        vaultAiopsEncrypt = new VaultEncrypt();
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            logger.error("Failed to read key or token file from aissemble-vault container. Error while converting inputSteam to a String.", e);
        }
        return "";
    }

    @After("@vaulthashing")
    public void shutdown() {
        genericContainer.stop();
    }

    @When("a data element is provided")
    public void a_data_element_is_provided() {
        logger.info("data element provided");
    }

    @When("the data element is encrypted")
    public void the_data_element_is_encrypted() {
        encryptedData = simpleAiopsEncrypt.encryptValue(dataElementVerbose);
        logger.info("AES ENCRYPTED DATA: " + encryptedData);
        assertNotNull("token could not be parsed!", encryptedData);
    }

    // This set of test steps should be ran as an integration step because of the Vault dependency
    @When("the data element is encrypted with vault")
    public void the_data_element_is_encrypted_with_vault() {
        encryptedData = vaultAiopsEncrypt.encryptValue(dataElement);
        logger.info("VAULT ENCRYPTED DATA: " + encryptedData);
        assertNotNull("Data could not be encrypted", encryptedData);
        assertNotEquals("Encrypted value was the same as the original value", dataElement, encryptedData);
    }

    @Then("a hash is generated")
    public void a_hash_is_generated() throws NoSuchAlgorithmException {
        byte[] salt = "some salt string".getBytes();
        String hashedValue = AiopsHash.getSHA1Hash(dataElement, salt);
        logger.info("data element hashed: " + hashedValue);
        assertEquals("8a19ae53e081c1a17bd3ca9b91413572781187f5", hashedValue);
    }

    @Then("decrypting it will return the original value")
    public void decrypting_it_will_return_the_original_value() {
        String decryptedVaultData = simpleAiopsEncrypt.decryptValue(encryptedData);
        logger.info("DECRYPTED AES DATA: " + decryptedVaultData);
        assertEquals("Decrypted value does not match the original value", dataElementVerbose, decryptedVaultData);
    }

    @Then("decrypting with vault it will return the original value")
    public void decrypting_with_vault_it_will_return_the_original_value() {
        String decryptedVaultData = vaultAiopsEncrypt.decryptValue(encryptedData);
        logger.warn("DECRYPTED VAULT DATA: " + decryptedVaultData);
        assertEquals("Decrypted value does not match the original value", dataElement, decryptedVaultData);
    }
}
