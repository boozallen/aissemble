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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.SparkStorageEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.config.deployment.spark.SparkDependencyConfiguration;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.java.JavaPipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SparkApplicationGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                           | Template                                                          | Generated File                          |
     * |----------------------------------|-------------------------------------------------------------------|-----------------------------------------|
     * | sparkApplicationDebugScript      | deployment/spark-operator/run-debug.sh.vm                         | apps/run-debug.sh                       |
     * | sparkApplicationValuesFile       | deployment/spark-operator/spark-application-base-values.yaml.vm   | apps/${pipelineName}-base-values.yaml   |
     * | sparkApplicationCIValuesFile     | deployment/spark-operator/spark-application-ci-values.yaml.vm     | apps/${pipelineName}-ci-values.yaml     |
     * | sparkApplicationDebugValuesFile  | deployment/spark-operator/spark-application-debug-values.yaml.vm  | apps/${pipelineName}-debug-values.yaml  |
     * | sparkApplicationDevValuesFile    | deployment/spark-operator/spark-application-dev-values.yaml.vm    | apps/${pipelineName}-dev-values.yaml    |
     * | sparkApplicationTestValuesFile   | deployment/spark-operator/spark-application-test-values.yaml.vm   | apps/${pipelineName}-test-values.yaml   |
     */

    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    private final SparkDependencyConfiguration config = SparkDependencyConfiguration.getInstance();
    protected static final String DOCKER_PROJECT_REPOSITORY_URL = "dockerProjectRepositoryUrl";

    private void handlePySpark(VelocityContext vc, GenerationContext context) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);
        PythonPipeline pythonTargetPipeline = new PythonPipeline(pipeline);

        String fileName = replace("pipelineName", context.getOutputFile(), pythonTargetPipeline.deriveArtifactIdFromCamelCase());
        context.setOutputFile(fileName);

        vc.put(VelocityProperty.SPARK_APPLICATION_TYPE, "Python");
        vc.put(VelocityProperty.MAIN_APPLICATION_FILE, pythonTargetPipeline.getKababCaseName() + "/" +
                pythonTargetPipeline.getSnakeCaseName() + "_driver.py");
    }

    private void handleSpark(VelocityContext vc, GenerationContext context) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);
        JavaPipeline javaTargetPipeline = new JavaPipeline(pipeline);

        String fileName = replace("pipelineName", context.getOutputFile(), javaTargetPipeline.deriveArtifactIdFromCamelCase());
        context.setOutputFile(fileName);

        vc.put(VelocityProperty.SPARK_APPLICATION_TYPE, "Java");
        vc.put(VelocityProperty.IS_JAVA_PIPELINE, true);
        vc.put(VelocityProperty.MAIN_APPLICATION_FILE, context.getArtifactId() + ".jar");
        vc.put(VelocityProperty.SPARK_MAIN_CLASS, context.getBasePackage() + "." + javaTargetPipeline.getCapitalizedName() + "Driver");
    }

    @Override
    public void generate(GenerationContext context) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        VelocityContext vc = getNewVelocityContext(context);
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);
        DataFlowStrategy dfs = new DataFlowStrategy(Collections.singletonList(pipeline));
        List<String> extensions = new ArrayList<>();
        if (dfs.isDeltaSupportNeeded()) {
            extensions.add("io.delta.sql.DeltaSparkSessionExtension");
        }
        if (dfs.isSedonaSupportNeeded()) {
            extensions.add("org.apache.sedona.sql.SedonaSqlExtensions");
        }
        if ("test".equalsIgnoreCase(context.getArtifactType())) {
            vc.put(VelocityProperty.IS_TEST_RESOURCE, true);
        }

        vc.put(VelocityProperty.ENABLE_PYSPARK_SUPPORT, dfs.isPySparkSupportNeeded());
        vc.put(VelocityProperty.ENABLE_RDBMS_SUPPORT, dfs.isRdbmsSupportNeeded());
        vc.put(VelocityProperty.ENABLE_NEO4J_SUPPORT, dfs.isNeo4jSupportNeeded());
        vc.put(VelocityProperty.ENABLE_DELTA_SUPPORT, dfs.isDeltaSupportNeeded());
        vc.put(VelocityProperty.ENABLE_ELASTICSEARCH_SUPPORT, dfs.isElasticsearchSupportNeeded());
        vc.put(VelocityProperty.ENABLE_HIVE_SUPPORT, dfs.isHiveSupportNeeded());
        vc.put(VelocityProperty.ENABLE_SEDONA_SUPPORT, dfs.isSedonaSupportNeeded());
        vc.put(VelocityProperty.ENABLE_DATA_LINEAGE_SUPPORT, dfs.isDataLineageNeeded());
        vc.put(VelocityProperty.SPARK_EXTENSIONS, String.join(",", extensions));

        vc.put("versionSpark", config.getSparkVersion());
        vc.put("versionDelta", config.getDeltaVersion());
        vc.put("versionElasticsearch", config.getElasticSearchVersion());
        vc.put("versionSedona", config.getSedonaVersion());
        vc.put("versionGeotools", config.getGeotoolsVersion());
        vc.put("versionPostgresql", config.getPostgresqlVersion());
        vc.put("versionMysqlConnector", config.getMysqlConnectorVersion());
        vc.put("versionHadoop", config.getHadoopVersion());
        vc.put("versionNeo4j", config.getNeo4jVersion());
        vc.put("versionAwsSdkBundle", config.getAwsSdkBundleVersion());

        // Addresses a Spark bug in which the --packages argument was not respected in cluster mode (3.0.0 <= x < 3.4.0)
        // We don't currently support Spark <3.x.x in aiSSEMBLE, but we include a check for it just in case we ever do.
        // Configuration option added in 1.5.0 (minimum).
        // If configuration option is NOT available in the provided version, direct URLs will be included in the
        // deps.jars argument.
        ComparableVersion provided = new ComparableVersion(config.getSparkVersion());
        ComparableVersion spark340 = new ComparableVersion("3.4.0");
        ComparableVersion spark300 = new ComparableVersion("3.0.0");
        ComparableVersion spark150 = new ComparableVersion("1.5.0");
        vc.put(VelocityProperty.USE_SPARK_PACKAGE_DEPS,
                provided.compareTo(spark150) >= 0 &&
                (provided.compareTo(spark340) >= 0 || provided.compareTo(spark300) < 0)
        );

        if (pipeline.getType().getImplementation().equalsIgnoreCase("data-delivery-spark")) {
            handleSpark(vc, context);
        } else if (pipeline.getType().getImplementation().equalsIgnoreCase("data-delivery-pyspark")) {
            handlePySpark(vc, context);
        } else {
            throw new IllegalArgumentException("Invalid implementation form: " + pipeline.getType().getImplementation());
        }

        final String projectName = context.getRootArtifactId();
        String dockerProjectRepositoryUrl = context.getPropertyVariables().get(DOCKER_PROJECT_REPOSITORY_URL);

        vc.put(VelocityProperty.SPARK_APPLICATION_NAME, context.getArtifactId());
        vc.put(VelocityProperty.PROJECT_NAME, projectName);
        vc.put(VelocityProperty.PIPELINE, pipeline.getName());
        vc.put(VelocityProperty.DOCKER_PROJECT_REPOSITORY_URL, dockerProjectRepositoryUrl);

        if (!"test".equalsIgnoreCase(context.getArtifactType()) && SparkStorageEnum.S3LOCAL == metamodelRepository.getDeploymentConfigurationManager().getSparkDeploymentConfiguration().getStorageType()) {
            vc.put(VelocityProperty.USE_S3_LOCAL, true);
            manualActionNotificationService.addDeployPomMessage(context, "s3local-deploy-v2", "s3-local");
            manualActionNotificationService.addPipelineInvocationServiceDeployment(context);
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(context, "pipeline-invocation");
            manualActionNotificationService.addNoticeToUpdateS3LocalConfig(context, "spark-infrastructure", Arrays.asList("spark-events/"));

        }

        generateFile(context, vc);
    }
}
