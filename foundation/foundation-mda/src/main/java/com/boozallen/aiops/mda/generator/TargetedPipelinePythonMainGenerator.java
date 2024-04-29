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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aissemble.common.Constants;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonStep;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilitates the generation of Python modules for the pipeline specified by the
 * {@code targetedPipeline} property in the {@code fermenter-mda} plugin configuration.
 */
public class TargetedPipelinePythonMainGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                             | Template                                               | Generated File                                   |
     * |------------------------------------|--------------------------------------------------------|--------------------------------------------------|
     * | inferenceApi                       | ${pipelineImplementation}/inference.api.py.vm          | inference_api.py                                 |
     * | pySparkAbstractDataActionImpl      | data-delivery-pyspark/abstract.data.action.impl.py.vm  | step/abstract_data_action_impl.py                |
     * | pySparkAbstractDataAction          | data-delivery-pyspark/abstract.data.action.py.vm       | step/abstract_data_action.py                     |
     * | pySparkAbstractPipelineStep        | data-delivery-pyspark/abstract.pipeline.step.py.vm     | step/abstract_pipeline_step.py                   |
     * | pySparkPipelineDriver              | data-delivery-pyspark/pipeline.driver.py.vm            | ${pipelineName}_driver.py                        |
     * | inferenceConfigScript              | general-mlflow/inference.config.py.vm                  | config/inference_config.py                       |
     * | inferenceRequest                   | general-mlflow/inference.request.py.vm                 | validation/request.py                            |
     * | inferenceResponse                  | general-mlflow/inference.response.py.vm                | validation/response.py                           |
     * | inferenceApiDriver                 | inference/inference.api.driver.py.vm                   | inference_api_driver.py                          |
     * | inferenceApiGrpcBase               | inference/inference.api.grpc.base.py.vm                | inference/grpc/inference_api_grpc_base.py        |
     * | inferenceApiRest                   | inference/inference.api.rest.py.vm                     | inference/rest/inference_api_rest.py             |
     * | inferenceImpl                      | inference/inference.impl.py.vm                         | inference_impl.py                                |
     * | inferenceMessageDefinitionBase     | inference/inference.message.base.py.vm                 | validation/inference_message_definition_base.py  |
     * | inferenceMessageDefinitionImpl     | inference/inference.message.impl.py.vm                 | validation/inference_message_definition.py       |
     * | inferencePayloadDefinitionImpl     | inference/inference.payload.impl.py.vm                 | validation/inference_payload_definition.py       |
     * | inferencePayloadDefinitionRest     | inference/inference.payload.rest.py.vm                 | inference/rest/inference_payload_definition.py   |
     * | modzyInferenceApi                  | modzy/modzy.py.vm                                      | modzy/modzy.py                                   |
     * | pySparkPipelineBase                | pipeline.base.py.vm                                    | pipeline/pipeline_base.py                        |
     * | pythonPipelineImplInit             | python.init.py.vm                                      | impl/__init__.py                                 |
     * | pySparkGeneratedInit               | python.init.py.vm                                      | __init__.py                                      |
     * | pySparkStepBaseInit                | python.init.py.vm                                      | step/__init__.py                                 |
     * | pySparkStepImplInit                | python.init.py.vm                                      | step/__init__.py                                 |
     * | generatedPostActionInit            | python.init.py.vm                                      | post_action/__init__.py                          |
     * | postActionInit                     | python.init.py.vm                                      | post_action/__init__.py                          |
     * | generatedBaseInit                  | python.init.py.vm                                      | __init__.py                                      |
     * | configInit                         | python.init.py.vm                                      | config/__init__.py                               |
     * | generatedInferenceBaseInit         | python.init.py.vm                                      | inference/__init__.py                            |
     * | generatedInferenceGrpcInit         | python.init.py.vm                                      | inference/grpc/__init__.py                       |
     * | generatedInferenceRestInit         | python.init.py.vm                                      | inference/rest/__init__.py                       |
     * | inferenceRestInit                  | python.init.py.vm                                      | inference/rest/__init__.py                       |
     * | generatedValidationInit            | python.init.py.vm                                      | validation/__init__.py                           |
     * | validationImplInit                 | python.init.py.vm                                      | validation/__init__.py                           |
     * | inferenceGrpcGeneratedGrpcPackage  | python.init.py.vm                                      | inference/grpc/generated/__init__.py             |
     */

    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    @Override
    public void generate(GenerationContext generationContext) {
        Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
        PythonPipeline pythonTargetPipeline = new PythonPipeline(pipeline);
        
        String originalTemplateName = generationContext.getTemplateName();
        String pipelineImplementation = pipeline.getType().getImplementation();
        String pipelineSpecificTemplateName = replace("pipelineImplementation", originalTemplateName, pipelineImplementation);
        generationContext.setTemplateName(pipelineSpecificTemplateName);
        
        VelocityContext vc = getNewVelocityContext(generationContext);
        vc.put(VelocityProperty.PIPELINE, pythonTargetPipeline);
        vc.put(VelocityProperty.ARTIFACT_ID_PYTHON_CASE, toPythonCase(generationContext.getArtifactId()));

        List<PythonStep> pythonSteps = new ArrayList<>();
        for (Step step : pythonTargetPipeline.getSteps()) {
            PythonStep pythonStep = new PythonStep(step);
            if (pythonTargetPipeline.getStepByType("inference") != null) {
                if (pythonStep.getType().equalsIgnoreCase("generic")) {
                    pythonSteps.add(pythonStep);
                }
            } else {
                pythonSteps.add(pythonStep);
            }
        }

        if(pythonTargetPipeline.getDataLineage()) {
            manualActionNotificationService.addNoticeToUpdateKafkaConfig(generationContext, Constants.DATA_LINEAGE_CHANNEL_NAME);
        }
        
        vc.put(VelocityProperty.STEPS, pythonSteps);

        String baseOutputFile = generationContext.getOutputFile();
        String fileName = replace("pipelineName", baseOutputFile, pythonTargetPipeline.getSnakeCaseName());
        generationContext.setOutputFile(fileName);
        generateFile(generationContext, vc);
    }

    private String toPythonCase(String toConvert) {
        return PipelineUtils.deriveLowerSnakeCaseNameFromHyphenatedString(toConvert);
    }

}
