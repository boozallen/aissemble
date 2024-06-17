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

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.mounts.MountPayload;
import com.bettercloud.vault.api.mounts.MountType;
import com.bettercloud.vault.response.LogicalResponse;
import com.bettercloud.vault.response.MountResponse;
import com.boozallen.aissemble.configuration.exception.PropertyDaoException;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.boozallen.aissemble.data.encryption.VaultUtil;
import com.boozallen.aissemble.data.encryption.config.DataEncryptionConfiguration;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * VaultPropertyDao reads and writes configuration property to Vault server
 */
@ApplicationScoped
public class VaultPropertyDao implements PropertyDao {
    private static final Logger logger = LoggerFactory.getLogger(VaultPropertyDao.class);

    @Override
    public boolean checkEmpty() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Read the property from Vault server with the given {@link PropertyKey} containing the group name and property name
     * @param PropertyKey property key
     * @return Property
     */
    @Override
    public Property read(PropertyKey propertyKey) {
        try {
            final Vault vault = getVault();
            // read from Vault
            final LogicalResponse readResponse = vault.logical()
                    .read(String.format("aissemble-properties/%s/%s", propertyKey.getGroupName(), propertyKey.getPropertyName()));

            return new Property(propertyKey, readResponse.getData().get(propertyKey.getPropertyName()));
        } catch (VaultException e) {
            throw new PropertyDaoException(String.format("Unable to read from vault with groupName: %s and property name: %s.", propertyKey.getGroupName(), propertyKey.getPropertyName()), e);
        }
    }

    /**
     * Write given property to the vault
     * @param property to be written to vault
     */
    @Override
    public void write(Property property) {
        if (property != null) {
            try {
                final Vault vault = getVault();
                this.write(vault, Collections.singletonList(property));
            } catch (VaultException e) {
                throw new PropertyDaoException(String.format("Unable to write to vault. Group name: %s, property name: %s", property.getGroupName(), property.getPropertyName()), e);
            }
        } else {
            throw new PropertyDaoException("Unable to write to vault with property: null");
        }
    }

    /**
     * Write given set of properties to the vault
     * @param properties to be written to vault
     */
    @Override
    public void write(Set<Property> properties) {
        if (properties != null && !properties.isEmpty()) {
            try {
                final Vault vault = getVault();
                this.write(vault, new ArrayList<>(properties));
            } catch (VaultException e) {
                throw new PropertyDaoException("Unable to write to configuration properties to vault.", e);
            }
        } else {
            throw new PropertyDaoException("Unable to write to vault with empty properties");
        }

    }

    private void write(Vault vault, List<Property> properties) throws VaultException {
        for (Property property : properties) {
            // path: aissemble-properties/<<group name>>
            String path = String.format("aissemble-properties/%s", property.getGroupName());
            // create a property secret engine
            createPropertiesSecretEngine(vault, path);

            Map<String, Object> data = new HashMap<>();
            data.put(property.getPropertyName(), property.getValue());

            // write key as a secret to Vault
            vault.logical().write(String.format("%s/%s", path, property.getPropertyName()), data);
            logger.info(String.format("Successfully wrote to vault: %s", path));
        }
    }


    private Vault getVault() throws VaultException {
        DataEncryptionConfiguration config = KrauseningConfigFactory.create(DataEncryptionConfiguration.class);
        final VaultConfig vaultConfig = new VaultConfig()
                .address(config.getSecretsHostUrl())
                .token(config.getSecretsRootKey())
                .prefixPathDepth(2) // aissemble-properties/<group name>/
                .engineVersion(2) // use version 2 to create properties as secret
                .build();

        final Vault vault = new Vault(vaultConfig);

        VaultUtil.checkSealStatusAndUnsealIfNecessary(vault);

        return vault;
    }

    private MountResponse createPropertiesSecretEngine(Vault vault, String path) throws VaultException {
        MountResponse response = null;
        try {
            response = vault.mounts().read(path);
        } catch (VaultException e) {
            if (e.getHttpStatusCode() == 400) {
                logger.info(String.format("Configuration properties secret engine does not exist on vault: %s", path));
            } else {
                logger.warn(String.format("Failed to fetch configuration properties secret engine on vault: %s", path));
            }
        }
        if (response == null || response.getRestResponse().getStatus() != 200) {
            //create a properties secret engine
            final MountPayload payload = new MountPayload()
                    .description(String.format("Configuration properties %s storage", path));
            response = vault.mounts().enable(path, MountType.KEY_VALUE_V2, payload);
        }

        return response;
    }
}
