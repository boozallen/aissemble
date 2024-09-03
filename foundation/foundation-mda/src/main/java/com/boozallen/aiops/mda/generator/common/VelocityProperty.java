package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

/**
 * Contains the variable names used in Velocity templates by MDA generation.
 */
public final class VelocityProperty {

    public static final String GROUP_ID = "groupId";
    public static final String PARENT_ARTIFACT_ID = "parentArtifactId";
    public static final String VERSION = "version";
    public static final String PARENT_DESCRIPTIVE_NAME = "parentDescriptiveName";
    public static final String SPARK_PIPELINES = "sparkPipelines";
    public static final String PYSPARK_PIPELINES = "pysparkPipelines";
    public static final String DATAFLOW_PIPELINES = "dataFlowPipelines";
    public static final String TRAINING_PIPELINE = "trainingPipeline";
    public static final String TRAINING_PIPELINES = "trainingPipelines";
    public static final String INFERENCE_PIPELINE = "inferencePipeline";
    public static final String INFERENCE_PIPELINES = "inferencePipelines";
    public static final String PYTHON_PIPELINE = "pythonPipeline";
    public static final String ARTIFACT_ID = "artifactId";
    public static final String ARTIFACT_ID_PYTHON_CASE = "artifactIdPythonCase";
    public static final String PROJECT_VERSION_PYTHON = "projectVersionPython";
    public static final String IS_TEST_RESOURCE = "isTestResource";
    /**
     * Captures the utilized version of aiSSEMBLE as a Python dependency version that
     * may be used in a Poetry pyproject.toml or pip requirements.txt specification
     */
    public static final String AISSEMBLE_VERSION = "aissembleVersion";
    public static final String AISSEMBLE_PYTHON_VERSION = "aissemblePythonVersion";
    public static final String PIPELINE = "pipeline";
    public static final String PIPELINES = "pipelines";
    public static final String BASE_PACKAGE = "basePackage";
    public static final String PYTHON_PACKAGE_NAME = "packageName";
    public static final String PYTHON_PACKAGE_FOLDER_NAME = "packageFolderName";
    public static final String AUTO_TRAIN = "autoTrain";
    public static final String STEP = "step";
    public static final String STEPS = "steps";
    public static final String FILE_STORE = "fileStore";
    public static final String HAS_FILE_STORE = "hasFileStore";
    public static final String FILE_STORE_NAMES = "fileStoreNames";
    public static final String ENABLE_DELTA_SUPPORT = "enableDeltaSupport";
    public static final String ENABLE_HIVE_SUPPORT = "enableHiveSupport";
    public static final String ENABLE_PYSPARK_SUPPORT = "enablePySparkSupport";
    public static final String ENABLE_SEDONA_SUPPORT = "enableSedonaSupport";
    public static final String ENABLE_POSTGRES_SUPPORT = "enablePostgresSupport";
    public static final String ENABLE_RDBMS_SUPPORT = "enableRDBMSSupport";
    public static final String ENABLE_ELASTICSEARCH_SUPPORT = "enableElasticsearchSupport";
    public static final String ENABLE_NEO4J_SUPPORT = "enableNeo4jSupport";
    public static final String SPARK_EXTENSIONS = "sparkExtensions";
    public static final String PIPELINE_ARTIFACT_ID = "pipelineArtifactId";
    public static final String PIPELINE_ARTIFACT_IDS = "pipelineArtifactIds";
    public static final String ROOT_ARTIFACT_ID = "rootArtifactId";
    public static final String ROOT_PIPELINE_ARTIFACT_ID = "rootPipelineArtifactId";
    public static final String ROOT_PIPELINE_ARTIFACT_ID_PYTHON_CASE = "rootPipelineArtifactIdPythonCase";
    public static final String MODULE_ARTIFACT_ID = "moduleArtifactId";
    public static final String MODULE_ARTIFACT_ID_PYTHON_CASE = "moduleArtifactIdPythonCase";
    public static final String DESCRIPTIVE_NAME = "descriptiveName";
    public static final String MACHINE_LEARNING_PIPELINE_ARTIFACT_IDS = "machineLearningPipelineArtifactIds";
    public static final String PIPELINE_STEP = "pipelineStep";
    public static final String PROJECT_GIT_URL = "projectGitUrl";
    public static final String STEP_ARTIFACT_ID = "stepArtifactId";
    public static final String STEP_ARTIFACT_ID_SNAKE_CASE = "stepArtifactIdSnakeCase";
    public static final String KUBERNETES_NAME = "kubernetesName";
    public static final String BEHAVE_FEATURE = "behaveFeature";
    public static final String RECORD = "record";
    public static final String RECORDS = "records";
    public static final String DICTIONARY = "dictionary";
    public static final String DICTIONARY_TYPE = "dictionaryType";
    public static final String POST_ACTION = "postAction";
    public static final String POST_ACTION_REQUIREMENTS = "postActionRequirements";
    public static final String SPARK_APPLICATION_TYPE = "sparkApplicationType";
    public static final String IS_JAVA_PIPELINE = "isJavaPipeline";
    public static final String MAIN_APPLICATION_FILE = "mainApplicationFile";
    public static final String SPARK_MAIN_CLASS = "mainClass";
    public static final String SPARK_APPLICATION_NAME = "sparkApplicationName";
    public static final String PROJECT_NAME = "projectName";
    public static final String ARTIFACT_ID_SNAKE_CASE = "artifactIdSnakeCase";
    public static final String INFERENCE_MODULE = "inferenceModule";
    public static final String INFERENCE_MODULE_SNAKE_CASE = "inferenceModuleSnakeCase";
    public static final String TRAINING_MODULE = "trainingModule";
    public static final String TRAINING_MODULE_SNAKE_CASE = "trainingModuleSnakeCase";
    public static final String TRAINING_PIPELINE_SNAKE_CASE = "trainingPipelineSnakeCase";
    public static final String VERSION_TAG = "versionTag";
    public static final String USE_S3_LOCAL = "useS3Local";
    public static final String ENABLE_DATA_LINEAGE_SUPPORT = "enableDataLineageSupport";
    public static final String ALERTING_SUPPORT_NEEDED = "alertingSupportNeeded";
    public static final String DATA_LINEAGE_CHANNEL_NAME = "dataLineageChannel";
    public static final String DATA_LINEAGE_STEPS_BY_PIPELINE = "dataLineageStepsByPipeline";
    public static final String USE_SPARK_PACKAGE_DEPS = "usePackageDeps";
    public static final String ENABLE_SEMANTIC_DATA_SUPPORT = "enableSemanticDataSupport";
    public static final String JAVA_DATA_RECORDS = "javaDataRecords";
    public static final String PYTHON_DATA_RECORDS = "pythonDataRecords";
    public static final String SCM_URL = "scmUrl";
    public static final String DOCKER_PROJECT_REPOSITORY_URL = "dockerProjectRepositoryUrl";

    private VelocityProperty() { }
}
