package com.boozallen.aiops.core.filestore;

/*-
 * #%L
 * AIOps Foundation::AIOps Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Map;
import java.util.Properties;

public interface FileStoreConfig {
    /**
     * Return the name of the provider. Value must be compatible with the JClouds provider option.
     * https://jclouds.apache.org/reference/providers
     * @return the provider
     */
    String getProvider();

    /**
     * Return the access key ID
     * @return the access key
     */
    String getAccessKeyId();

    /**
     * Return the secret key
     * @return the secret key
     */
    String getSecretAccessKey();

    /**
     * Return additional overrides. See https://jclouds.apache.org/ for more details.
     * @return property overrides
     */
    Properties getOverrides();
}
