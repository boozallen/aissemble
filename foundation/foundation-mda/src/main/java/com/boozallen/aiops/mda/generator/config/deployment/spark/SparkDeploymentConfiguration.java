package com.boozallen.aiops.mda.generator.config.deployment.spark;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
