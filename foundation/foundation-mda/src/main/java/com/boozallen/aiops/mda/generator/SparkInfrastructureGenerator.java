package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.SparkStorageEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

public class SparkInfrastructureGenerator extends KubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                               | Template                                                                        | Generated File                   |
     * |--------------------------------------|---------------------------------------------------------------------------------|----------------------------------|
     * | hiveMetastoreServiceValuesDevFile    | deployment/hive-metastore-service/hive-metastore-service-values-dev.yaml.vm     | apps/${appName}/values-dev.yaml  |
     * | hiveMetastoreServiceValuesFile       | deployment/hive-metastore-service/hive-metastore-service-values.yaml.vm         | apps/${appName}/values.yaml      |
     * | hiveMetastoreServiceValuesDevFileV2  | deployment/hive-metastore-service/v2/hive-metastore-service-values-dev.yaml.vm  | apps/${appName}/values-dev.yaml  |
     * | hiveMetastoreServiceValuesFileV2     | deployment/hive-metastore-service/v2/hive-metastore-service-values.yaml.vm      | apps/${appName}/values.yaml      |
     * | sparkInfrastructureValuesDevFile     | deployment/spark-infrastructure/spark.infrastructure.values-dev.yaml.vm         | apps/${appName}/values-dev.yaml  |
     * | sparkInfrastructureValuesFile        | deployment/spark-infrastructure/spark.infrastructure.values.yaml.vm             | apps/${appName}/values.yaml      |
     */

    public void generate(GenerationContext context) {
        VelocityContext vc = super.configureWithoutGeneration(context);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        if (metamodelRepository.getDeploymentConfigurationManager().getSparkDeploymentConfiguration().getStorageType() == SparkStorageEnum.S3LOCAL) {
            vc.put(VelocityProperty.USE_S3_LOCAL, true);
        }

        generateFile(context, vc);
    }
}
