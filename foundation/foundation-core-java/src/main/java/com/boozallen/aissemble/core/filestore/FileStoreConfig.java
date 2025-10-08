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
