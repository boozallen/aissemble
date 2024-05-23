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
import com.boozallen.aiops.mda.metamodel.element.python.PythonStep;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.lang.String; 
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enables the generation of scaffolding code for a machine learning (training)
 * pipeline.
 */
public class MachineLearningPipelineGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                         | Template                                | Generated File             |
     * |--------------------------------|-----------------------------------------|----------------------------|
     * | trainingPipelineBase           | general-mlflow/training.base.py.vm      | ${pipelineName}_base.py    |
     * | trainingPipelineConfigScript   | general-mlflow/training.config.py.vm    | config/pipeline_config.py  |
     * | trainingPipelineDriver         | general-mlflow/training.driver.py.vm    | ${pipelineName}_driver.py  |
     * | trainingPipelineImpl           | general-mlflow/training.impl.py.vm      | impl/${pipelineName}.py    |
     * | pipelineBase                   | pipeline.base.py.vm                     | pipeline/pipeline_base.py  |
     * | sagemakerTrainingPipelineImpl  | sagemaker-training/training.impl.py.vm  | ${pipelineName}.py         |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        Pipeline targetPipeline = PipelineUtils.getTargetedPipeline(context, metadataContext);
        MachineLearningPipeline mlPipeline = new MachineLearningPipeline(targetPipeline);
        VelocityContext vc = getNewVelocityContext(context);

        /*
         * NOTE: This is intentionally (but temporarily) brittle.  In the not so distant future, training is
         * likely to be split out into a separate pipeline type from inference, enabling input cleaning, etc.  At
         * present, however, there is no intuitive way of delineating between steps targeted at the training
         * pipeline versus the inference pipeline.  The assumption at present is that all generic steps prior to
         * training are specific to training, and all steps following training are targeted at inference.  The
         * output code is lightweight enough that user modifications to the generated code should be trivial, and
         * the lifespan of the current joint-pipeline pattern is expected to be short enough that this doesn't last
         * very long. //pm 3/5/23
         */
        List<PythonStep> genericSteps = mlPipeline.getSteps().stream()
                .takeWhile(PipelineUtils::isGenericStep)
                .map(PythonStep::new)
                .collect(Collectors.toList());

        vc.put(VelocityProperty.STEPS, genericSteps);

        vc.put(VelocityProperty.PIPELINE, mlPipeline);

        boolean enableAutoTrain = enableAutoTrain(mlPipeline);
        vc.put(VelocityProperty.AUTO_TRAIN, enableAutoTrain);

        vc.put("containsOnnx", pipelineContainsOnnxPostAction(mlPipeline));

        String baseOutputFile = context.getOutputFile();
        String fileName = replace("pipelineName", baseOutputFile, mlPipeline.getSnakeCaseName());
        context.setOutputFile(fileName);
        generateFile(context, vc);
    }


    private boolean enableAutoTrain(MachineLearningPipeline pipeline) {
        boolean enableAutoTrain = false;

        Step trainingStep = pipeline.getTrainingStep();
        if (trainingStep != null && trainingStep.getInbound() != null) {
            enableAutoTrain = true;
        }

        return enableAutoTrain;
    }

    private boolean pipelineContainsOnnxPostAction(MachineLearningPipeline pipeline) {
        boolean containsOnnx = false;

        for (PostAction postAction : pipeline.getTrainingStep().getPostActions()) {
            if (PipelineUtils.forOnnxModelConversion(postAction)) {
                containsOnnx = true;
                break;
            }
        }
        return containsOnnx;
    }

}
