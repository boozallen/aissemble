package com.boozallen.aissemble.core.filestore;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
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
