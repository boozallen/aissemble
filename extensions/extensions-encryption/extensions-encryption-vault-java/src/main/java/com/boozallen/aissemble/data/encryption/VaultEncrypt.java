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

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.LogicalResponse;
import com.boozallen.aissemble.data.encryption.config.DataEncryptionConfiguration;
import com.boozallen.aissemble.data.encryption.exception.AiopsEncryptException;

import org.aeonbits.owner.KrauseningConfigFactory;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * Utility class for common encryption operations using a Vault SaaS.
 */
public class VaultEncrypt implements AiopsEncrypt {
    private final DataEncryptionConfiguration config = KrauseningConfigFactory.create(DataEncryptionConfiguration.class);

    /**
     * Encrypts a string
     *
     * @param valueToEncrypt the string to encrypt
     * @return the encrypted string
     */
    public String encryptValue(String valueToEncrypt) {
        String encryptedValue;
        try {
            final VaultConfig vaultConfig = new VaultConfig()
                    .address(config.getSecretsHostUrl())
                    .token(config.getSecretsRootKey())
                    .engineVersion(1)
                    .build();

            final Vault vault = new Vault(vaultConfig);

            VaultUtil.checkSealStatusAndUnsealIfNecessary(vault);

            // Base64 encoding is required by Vault
            String base64EncodedDataToEncrypt = base64Encode(valueToEncrypt);
            LogicalResponse response = vault.logical().write("transit/encrypt/aissemblekey",
                    Collections.singletonMap("plaintext", base64EncodedDataToEncrypt));

            Map<String, String> responses = response.getData();
            encryptedValue = responses.get("ciphertext");
        } catch (VaultException e) {
            throw new AiopsEncryptException("Unable to encrypt value. Please check the encrypt.properties file for current root token and unseal keys.", e);
        }

        return encryptedValue;
    }

    /**
     * Decrypts a string
     *
     * @param valueTodecrypt the string to decrypt
     * @return the decrypted string
     */
    public String decryptValue(String valueTodecrypt) {
        String decryptedValue;
        try {
            final VaultConfig vaultConfig = new VaultConfig()
                    .address(config.getSecretsHostUrl())
                    .token(config.getSecretsRootKey())
                    .engineVersion(1)
                    .build();

            final Vault vault = new Vault(vaultConfig);

            VaultUtil.checkSealStatusAndUnsealIfNecessary(vault);

            LogicalResponse response = vault.logical().write("transit/decrypt/aissemblekey",
                    Collections.singletonMap("ciphertext", valueTodecrypt));

            Map<String, String> responses = response.getData();

            // The decrypted value will be Base64 encoded so we will decode it here
            decryptedValue = base64Decode(responses.get("plaintext"));
        } catch (VaultException e) {
            throw new AiopsEncryptException("Unable to decrypt value. Please check the encrypt.properties file for current root token and unseal keys.", e);
        }

        return decryptedValue;
    }

    public String base64Encode(String value) {
        byte[] encodedBytes = Base64.getEncoder().encode(value.getBytes());

        return new String(encodedBytes);
    }

    public String base64Decode(String value) {
        byte[] decodedBytes = Base64.getDecoder().decode(value);

        return new String(decodedBytes);
    }
}

