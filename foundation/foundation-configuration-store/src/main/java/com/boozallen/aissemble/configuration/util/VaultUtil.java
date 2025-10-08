package com.boozallen.aissemble.configuration.util;

/*-
 * #%L
 * aiSSEMBLE Data Encryption::Encryption (Java)
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.SealResponse;
import com.boozallen.aissemble.configuration.config.ConfigStoreVaultConfiguration;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for interacting with a Vault server.
 */
public final class VaultUtil {

    private static final Logger logger = LoggerFactory.getLogger(VaultUtil.class);
    private static final ConfigStoreVaultConfiguration config = KrauseningConfigFactory.create(ConfigStoreVaultConfiguration.class);

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

