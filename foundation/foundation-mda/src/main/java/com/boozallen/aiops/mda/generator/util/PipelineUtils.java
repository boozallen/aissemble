package com.boozallen.aiops.mda.generator.util;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.DataFlowStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.post.action.ModelConversionType;
import com.boozallen.aiops.mda.generator.post.action.PostActionType;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PostAction;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Common utilities for pipelines.
 */
public final class PipelineUtils {

    private static final String TARGET_PIPELINE = "targetPipeline";

    /**
     * Exposes the utilized version of aiSSEMBLE from {@code fermenter-mda} plugin's
     * {@code <propertyVariable>} declaration such that it may be used in templates.
     */
    protected static final String AISSEMBLE_VERSION = "aissembleVersion";

    private static final Logger logger = LoggerFactory.getLogger(PipelineUtils.class);

    private PipelineUtils() {
        // prevent local instantiation of a static class
    }

    /**
     * Returns the targeted pipeline for generation.
     *
     * @param generationContext
     *            context in which the instance is generating
     * @return the target pipeline
     */
    public static Pipeline getTargetedPipeline(GenerationContext generationContext, String metadataContext) {
        Map<String, String> generationPropertyVariables = generationContext.getPropertyVariables();
        String targetPipelineName = generationPropertyVariables.get(TARGET_PIPELINE);
        assertTargetPipelineConfigurationFound(targetPipelineName);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        Pipeline targetPipeline = pipelineMap.get(targetPipelineName);
        assertTargetPipelineExists(targetPipelineName, targetPipeline);
        return targetPipeline;
    }

    public static String getQualifiedPipelineStepName(Pipeline pipeline, Step step) {
        return pipeline.getName() + "." + step.getName();
    }

    private static void assertTargetPipelineConfigurationFound(String targetedPipelineName) {
        if (StringUtils.isBlank(targetedPipelineName)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n<plugin>\n");
            sb.append("\t<groupId>org.technologybrewery.fermenter</groupId>\n");
            sb.append("\t<artifactId>fermenter-mda</artifactId>\n");
            sb.append("\t...\n");
            sb.append("\t<configuration>\n");
            sb.append("\t\t<propertyVariables>\n");
            sb.append("\t\t\t<").append(TARGET_PIPELINE).append(">PIPELINE NAME HERE</").append(TARGET_PIPELINE)
                    .append(">   <-----------  MISSING VALUE!\n");
            sb.append("\t\t</propertyVariables>\n");
            sb.append("\t\t...\n");
            sb.append("\t</configuration>\n");
            sb.append("</plugin>\n");
            String errorMessage = sb.toString();
            logger.error(errorMessage);

            throw new GenerationException("A targetedPipeline value MUST be provided!");
        }
    }

    private static void assertTargetPipelineExists(String targetedPipelineName, Pipeline targetedPipeline) {
        if (targetedPipeline == null) {
            throw new GenerationException("Target pipeline '" + targetedPipelineName + "' could not be found!");
        }
    }

    public static boolean isAsynchronousStep(Step step) {
        return "asynchronous".equals(step.getType());
    }

    public static boolean isSynchronousStep(Step step) {
        return "synchronous".equalsIgnoreCase(step.getType());
    }

    public static boolean isGenericStep(Step step) {
        return "generic".equalsIgnoreCase(step.getType());
    }

    /**
     * Returns the record name formatted into lowercase with hyphens from upper camel case.
     * @param camelCasedString a string in upper camel case format
     * @return the record name formatted into lowercase with hyphens
     */
    public static String deriveArtifactIdFromCamelCase(String camelCasedString) {
        String acronymCleanString = normalizeAcronyms(camelCasedString);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, acronymCleanString);
    }

    /**
     * Returns the record name formatted into lowercase with underscores from upper camel case.
     * (Python naming convention).
     * @param camelCasedString a string in upper camel case format
     * @return the record name formatted into lowercase with underscores
     */
    public static String deriveLowercaseSnakeCaseNameFromCamelCase(String camelCasedString) {
        String acronymCleanString = normalizeAcronyms(camelCasedString);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, acronymCleanString);
    }

    /**
     * Returns the record name formatted into lowercase with underscores from lowercase with hyphens.
     * (Python naming convention)
     * @param hyphenatedString a lower case hyphenated string
     * @return the record name formatted into lowercase with underscores
     */
    public static String deriveLowerSnakeCaseNameFromHyphenatedString(String hyphenatedString) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, hyphenatedString);
    }

    /**
     * Returns the record name formatted into lower camel case from upper camel case.
     * @param camelCasedString a string in upper camel case format
     * @return the record name formatted into lower camel case
     */
    public static String deriveLowerCamelNameFromUpperCamelName(String camelCasedString) {
        String acronymCleanString = normalizeAcronyms(camelCasedString);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, acronymCleanString);
    }

    /**
     * Returns the record name formatted into upper underscore from upper camel case.
     * @param camelCasedString a string in upper camel case format
     * @return the record name formatted into upper underscore case
     */
    public static String deriveUpperUnderscoreNameFromUpperCamelName(String camelCasedString) {
        String acronymCleanString = normalizeAcronyms(camelCasedString);
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, acronymCleanString);
    }

    /**
     * Returns the record name formatted into upper camel case from lower case with underscores.
     * @param lowerUnderscoreString a lowercase string with underscores
     * @return the record name formatted into upper camel case
     */
    public static String deriveUpperCamelNameFromLowerUnderscoreName(String lowerUnderscoreString) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, lowerUnderscoreString);
    }

    /**
     * Helper method that extracts the utilized version of aiSSEMBLE, which is specified
     * via the {@code fermenter-mda} plugin's corresponding {@code <propertyVariables>}
     * declaration.
     *
     * @param generationContext relevant {@code fermenter-mda} generation context.
     * @return version of aiSSEMBLE that is specified via {@code fermenter-mda}'s
     * {@link #AISSEMBLE_VERSION} {@code <propertyVariables>} declaration.
     */
    public static String getAiSSEMBLEVersion(GenerationContext generationContext) {
        return generationContext.getPropertyVariables().get(AISSEMBLE_VERSION);
    }

    /**
     * Returns all the Data Delivery pipelines using Airflow as the execution helper
     * @return List of {@link Pipeline}
     */
    public static List<Pipeline> getDataFlowPipelinesRequiringAirflow(List<Pipeline> pipelines){
        List<Pipeline> airflowPipelines = new ArrayList<Pipeline>();
        for(Pipeline pipeline:pipelines) {
            if(pipeline.requiresAirflow() && PipelineEnum.DATA_FLOW.equalsIgnoreCase(pipeline.getType().getName())) {
                airflowPipelines.add(pipeline);
            }
        }
        return airflowPipelines;
    }

    /**
     * Returns all the Machine Learning pipelines using Airflow as the execution helper
     * @return List of {@link Pipeline}
     */
    public static List<Pipeline> getMachineLearningPipelinesRequiringAirflow(List<Pipeline> pipelines){
        List<Pipeline> airflowPipelines = new ArrayList<Pipeline>();
        for(Pipeline pipeline:pipelines) {
            if(pipeline.requiresAirflow() && PipelineEnum.MACHINE_LEARNING.equalsIgnoreCase(pipeline.getType().getName())) {
                airflowPipelines.add(pipeline);
            }
        }
        return airflowPipelines;
    }

    /**
     * Returns the list of pipelines for the current metadata context.
     *
     * @return the list of pipelines
     * @param metadataContext
     */
    public static List<Pipeline> getPipelines(String metadataContext) {
        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);
        return new ArrayList<>(pipelineMap.values());
    }

    public static DataFlowStrategy getDataFlowPipelines(String metadataContext) {
        return new DataFlowStrategy(getPipelines(metadataContext));
    }

    /**
     * This method returns a string in camel case whether an acronym is present or not
     * Examples:
     *       | pipeline       | output           |
     *       | UsgsPipeline   | UsgsPipeline    |
     *       | USGSPipeline   | UsgsPipeline    |
     *       | My123Pipeline  | My123Pipeline   |
     *       | MyTESTPipeline | MyTestPipeline |
     *       | MyPipelineTEST | MyPipelineTest |
     * @param camelCasedString with acronyms or not
     * @return String in camel case
     */
    private static String normalizeAcronyms(String camelCasedString) {
        StringBuilder normalizedString = new StringBuilder();
        String[] splitStrings = StringUtils.splitByCharacterTypeCamelCase(camelCasedString);

        for (String segment : splitStrings) {
            normalizedString.append(StringUtils.capitalize(StringUtils.lowerCase(segment)));
        }
        return normalizedString.toString();
    }

    /**
     * Helper method that returns a {@link Boolean} value indicating whether the given {@link PostAction} is for
     * an ONNX model conversion.
     *
     * @param postAction post action metamodel for which to determine if it is related to an ONNX model conversion
     * @return whether the given {@link PostAction} is for an ONNX model conversion.
     */
    public static boolean forOnnxModelConversion(PostAction postAction) {
        boolean forOnnxModelConversion = false;
        PostActionType postActionType = PostActionType.getPostActionType(postAction);
        if (postActionType == PostActionType.MODEL_CONVERSION) {
            ModelConversionType modelConversionType = ModelConversionType.getModelConversionType(postAction);
            if (modelConversionType == ModelConversionType.ONNX) {
                forOnnxModelConversion = true;
            }
        }

        return forOnnxModelConversion;
    }
}
