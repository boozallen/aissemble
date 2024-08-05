package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PostAction;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.MachineLearningPipeline;
import com.boozallen.aiops.mda.metamodel.element.training.OnnxModelConversionPostAction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link TargetedPipelinePyProjectGenerator} that generates a {@code pyproject.toml} specifically for the training
 * step modules of machine learning pipelines.
 */
public class MlTrainingPyProjectGenerator extends TargetedPipelinePyProjectGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                     | Template                          | Generated File  |
     * |----------------------------|-----------------------------------|-----------------|
     * | trainingPipelinePyProject  | general-mlflow/pyproject.toml.vm  | pyproject.toml  |
     */


    // additional requirements needed if there are onnx model conversion post
    // actions added to the training step
    private static final String ONNX_ML_TOOLS_DEPENDENCY = "onnxmltools = \"^1.11.1\"";
    private static final String ONNX_KERAS_DEPENDENCY = "tf2onnx = \"^1.12.1\"";

    /**
     * Populates the given {@link VelocityContext} with any necessary machine learning training pipeline specific
     * attributes, generates the project's {@code pyproject.toml}, and provides a notification to users if manual
     * modification to the {@code pyproject.toml} is needed.
     *
     * @param generationContext Fermenter generation context.
     * @param velocityContext   pre-populated context that contains commonly used attributes.
     * @param pipeline          targeted pipeline for which this generator is being applied.
     */
    @Override
    protected void doGenerateFile(GenerationContext generationContext, VelocityContext velocityContext, Pipeline pipeline) {
        MachineLearningPipeline mlPipeline = new MachineLearningPipeline(pipeline);
        Step trainingStep = mlPipeline.getTrainingStep();

        Set<String> postActionDependencies = null;
        velocityContext.put(VelocityProperty.PIPELINE, mlPipeline);
        List<PostAction> postActions = trainingStep.getPostActions();
        if (CollectionUtils.isNotEmpty(postActions)) {
            postActionDependencies = getPostActionDependencies(postActions);
            velocityContext.put(VelocityProperty.POST_ACTION_REQUIREMENTS, postActionDependencies);
        }

        generateFile(generationContext, velocityContext);

        if (CollectionUtils.isNotEmpty(postActionDependencies)) {
            manualActionNotificationService.addNoticeToAddPythonDependencies(generationContext, postActionDependencies, "post action support");
        }
    }

    /**
     * Retrieves any additional Python package dependencies that are required to support the provided
     * {@link PostAction}s.
     *
     * @param postActions post actions for the targeted pipeline for which to retrieve any additional
     *                    needed dependencies.
     * @return additional Python package dependencies needed to support the provided {@link PostAction}s,
     * formatted as Poetry {@code pyproject.toml} dependency specifications.
     */
    protected Set<String> getPostActionDependencies(List<PostAction> postActions) {
        Set<String> postActionRequirements = new LinkedHashSet<>();
        for (PostAction postAction : postActions) {
            if (PipelineUtils.forOnnxModelConversion(postAction)) {
                postActionRequirements.add(ONNX_ML_TOOLS_DEPENDENCY);

                String modelSource = postAction.getModelSource();
                if (OnnxModelConversionPostAction.KERAS.equals(modelSource)) {
                    postActionRequirements.add(ONNX_KERAS_DEPENDENCY);
                }
            }
        }

        return postActionRequirements;
    }

}
