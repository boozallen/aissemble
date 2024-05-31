package com.boozallen.aissemble.core.filestore;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Properties;

/**
 * Class used to mock the file store config. Used for testing purposes only to avoid
 * configuring environment variables for tests.
 */
public class MockFileStoreConfig extends AbstractFileStoreConfig {

    private String provider = "";
    private String accessKeyId = "";
    private String secretAccessKey = "";
    private Properties overrides = new Properties();

    public MockFileStoreConfig(String name) {
        super(name);
    }

    @Override
    public String getProvider() {
        return provider;
    }

    public void setProvider(final String provider) {
        this.provider = provider;
    }

    @Override
    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(final String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Override
    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(final String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    @Override
    public Properties getOverrides() {
        return overrides;
    }

    public void setOverrides(final Properties overrides) {
        this.overrides = overrides;
    }
}
