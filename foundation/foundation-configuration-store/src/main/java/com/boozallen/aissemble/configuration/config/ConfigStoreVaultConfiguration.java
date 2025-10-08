package com.boozallen.aissemble.configuration.config;

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

import org.aeonbits.owner.KrauseningConfig;

/**
 * Configurations for config store vault server access.
 */
@KrauseningConfig.KrauseningSources("config-store-vault.properties")
public interface ConfigStoreVaultConfiguration extends KrauseningConfig {

    /**
     * Returns the URL to the Vault server.
     * 
     * @return vault server url
     */
    @Key("secrets.host.url")
    @DefaultValue("http://127.0.0.1:8217")
    String getSecretsHostUrl();

    /**
     * Returns the root key for the Vault server.
     * 
     * @return vault root key
     */
    @Key("secrets.root.key")
    @DefaultValue("rootkey")
    String getSecretsRootKey();

    /**
     * Returns the unseal keys for the Vault server.
     * 
     * @return vault unseal keys
     */
    @Key("secrets.unseal.keys")
    @DefaultValue("key1,key2,key3")
    String getSecretsUnsealKeys();

}
