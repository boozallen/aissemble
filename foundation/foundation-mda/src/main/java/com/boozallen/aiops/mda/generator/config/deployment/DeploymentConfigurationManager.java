package com.boozallen.aiops.mda.generator.config.deployment;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.config.deployment.spark.SparkDeploymentConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class DeploymentConfigurationManager {
    @JsonProperty("spark")
    @JsonSetter(nulls = Nulls.SKIP)
    private SparkDeploymentConfiguration sparkDeploymentConfiguration;

    public DeploymentConfigurationManager() {
        sparkDeploymentConfiguration = new SparkDeploymentConfiguration();
    }

    public SparkDeploymentConfiguration getSparkDeploymentConfiguration() {
        return sparkDeploymentConfiguration;
    }
}
