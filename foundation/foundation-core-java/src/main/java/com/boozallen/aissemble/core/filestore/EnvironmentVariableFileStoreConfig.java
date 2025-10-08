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

import com.google.gson.Gson;

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class EnvironmentVariableFileStoreConfig extends AbstractFileStoreConfig {

    private Map<String, String> config;


    public EnvironmentVariableFileStoreConfig(final String name) {
        super(name);
        this.config = getConfig(name);
    }

    /**
     * Get the configurations for the given filestore name.
     *
     * @param name the name of the filestore
     * @return the configurations from the environment variables.
     */
    public Map<String, String> getConfig(String name) {
        final Map<String, String> values = System.getenv();
        return values.entrySet()
                .stream()
                .filter(envVar -> envVar.getKey().startsWith(name))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public String getProvider() {
        return this.config.get(getName() + "_FS_PROVIDER");
    }

    @Override
    public String getAccessKeyId() {
        return this.config.get(getName() + "_FS_ACCESS_KEY_ID");
    }

    @Override
    public String getSecretAccessKey() {
        return this.config.get(getName() + "_FS_SECRET_ACCESS_KEY");
    }

    @Override
    public Properties getOverrides() {
        // Convert namespaced override values to property names understood by JClouds
        final String overridesJsonString = this.config.get(getName() + "_FS_OVERRIDES");
        final Properties propertyOverrides = new Properties();
        if (overridesJsonString != null && !overridesJsonString.isBlank()) {
            final Gson gson = new Gson();
            final Map overrides = gson.fromJson(overridesJsonString, Map.class);
            propertyOverrides.putAll(overrides);
        }
        return propertyOverrides;
    }
}
