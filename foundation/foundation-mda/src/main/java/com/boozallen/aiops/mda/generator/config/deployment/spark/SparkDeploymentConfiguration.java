package com.boozallen.aiops.mda.generator.config.deployment.spark;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import com.boozallen.aiops.mda.generator.config.deployment.DeploymentConfiguration;
import com.boozallen.aiops.mda.generator.common.SparkStorageEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;


public class SparkDeploymentConfiguration implements DeploymentConfiguration {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSetter(nulls = Nulls.SKIP)
    private String storage;

    public SparkDeploymentConfiguration() {
        this.storage = SparkStorageEnum.S3LOCAL.name();
    }

    public SparkStorageEnum getStorageType() {
        return SparkStorageEnum.valueOf(storage.toUpperCase());
    }
}
