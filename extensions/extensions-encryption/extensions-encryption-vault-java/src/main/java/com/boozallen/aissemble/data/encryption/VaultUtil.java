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
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.SealResponse;
import com.boozallen.aissemble.data.encryption.config.DataEncryptionConfiguration;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for interacting with a Vault server.
 */
public final class VaultUtil {

    private static final Logger logger = LoggerFactory.getLogger(VaultUtil.class);
    private static final DataEncryptionConfiguration config = KrauseningConfigFactory.create(DataEncryptionConfiguration.class);

    public static void checkSealStatusAndUnsealIfNecessary(Vault vault) throws VaultException {
        // Check to see if the Vault service is sealed
        SealResponse initialSealStatus = vault.seal().sealStatus();
        if(initialSealStatus.getSealed()) {
            String[] unsealKeys = config.getSecretsUnsealKeys().split(",");
            for(String unsealKey: unsealKeys) {
                SealResponse sealResponse = vault.seal().unseal(unsealKey);
                if(sealResponse.getProgress() < 1) {
                    // We won't know the number of keys required to unseal the vault until we
                    // provide at least one valid unseal key.
                    break;
                }
            }
            logger.info("DONE UNSEALING");
        }
    }
}

