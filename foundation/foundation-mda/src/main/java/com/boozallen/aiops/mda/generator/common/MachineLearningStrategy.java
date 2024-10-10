package com.boozallen.aiops.mda.generator.common;

import com.boozallen.aiops.mda.generator.util.PipelineUtils;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.BaseStepDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.Versioning;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;

import java.util.ArrayList;
import java.util.List;

import static com.boozallen.aiops.mda.generator.util.PipelineUtils.deriveArtifactIdFromCamelCase;

public class MachineLearningStrategy extends AbstractStrategy {

    public MachineLearningStrategy(List<Pipeline> pipelines) {
        super(pipelines, PipelineEnum.MACHINE_LEARNING);
    }

    /**
     * This method gets the training steps across all machine-learning pipelines.
     * @return steps with their associated pipeline
     */
    @Override
    public List<PipelineStepPair> getSteps() {
        // the training module contains the core pipeline implementation
        // which is why this returns only training steps
        return getStepsByType("training");
    }

    /**
     * Gets the inference steps across all machine-learning pipelines.
     * @return
     */
    public List<PipelineStepPair> getInferenceSteps() {
        return getStepsByType("inference");
    }
    

    /**
     * Gets the list of inference modules.
     * 
     * @return the list of inference modules
     */
    public List<String> getInferenceModules() {
        List<String> inferenceModules = new ArrayList<>();
        for (PipelineStepPair pair : getInferenceSteps()) {
            String moduleName = pair.getStep().getName();
            String moduleArtifactId = deriveArtifactIdFromCamelCase(moduleName);
            inferenceModules.add(moduleArtifactId);

        }

        return inferenceModules;
    }

    /**
     * Gets the list of training modules.
     * 
     * @return the list of training modules
     */
    public List<String> getTrainingModules() {
        List<String> trainingModules = new ArrayList<>();
        for (PipelineStepPair pair : getSteps()) {
            String moduleName = pair.getStep().getName();
            String moduleArtifactId = deriveArtifactIdFromCamelCase(moduleName);
            trainingModules.add(moduleArtifactId);

        }

        return trainingModules;
    }

    /**
     * Returns all the Machine Learning pipelines using Airflow as the execution helper
     * @return List of {@link BasePipelineDecorator} instantiation of the pipelines
     */
    public List<BasePipelineDecorator> getMachineLearningPipelinesRequiringAirflow() {
        List<BasePipelineDecorator> decoratedPipelines = new ArrayList<BasePipelineDecorator>();

        for (Pipeline pipeline : PipelineUtils.getMachineLearningPipelinesRequiringAirflow(pipelines)) {
            BasePipelineDecorator decoratedPipeline = new BasePipelineDecorator(pipeline);
            List<BaseStepDecorator> pipelineSteps = decoratedPipeline.getSteps();

            for (Step step : pipelineSteps) {
                if (step.getType().equalsIgnoreCase("training")) {
                    decoratedPipelines.add(decoratedPipeline);
                }
            }         
        }
        return decoratedPipelines;
    }

    /**
     * This method determines if airflow support is needed (server resources, pom pomfile, etc...)
     *
     * @return true if airflow support is needed, false otherwise
     */
    public boolean isAirflowNeeded() {
        return (getMachineLearningPipelinesRequiringAirflow()!=null && getMachineLearningPipelinesRequiringAirflow().size()>0);
    }

    /**
     * This method determines if mlflow support is needed (server resources, pom pomfile, etc...)
     *
     * @return true if mlflow support is needed, false otherwise
     */
    public boolean isMlflowNeeded() {
        boolean mlflowSupportNeeded = hasInferenceOrTrainingPipelineStep();

        return mlflowSupportNeeded;
    }
    
    /**
     * This method determines if model training API support is needed (server resources, pom pomfile, etc...)
     *
     * @return true if model training api support is needed, false otherwise
     */
    public boolean isModelTrainingApiNeeded() {
        boolean modelTrainingApiSupportNeeded = hasTrainingPipelineStep();

        return modelTrainingApiSupportNeeded;
    }

    /**
     * This method determines if postgres support is needed
     * Currently tied to mlflow as a dependency
     *
     * @return true if postgres support is needed, false otherwise
     */
    public boolean isPostgresNeeded() {
        // Currently Postgres is needed if Mlflow is needed
        boolean postgresSupportNeeded = isMlflowNeeded();

        return postgresSupportNeeded;
    }

    /**
     * Checks whether there are any pipeline steps that is of training type.
     *
     * @return true if a pipeline exists with a training step
     */
    private boolean hasTrainingPipelineStep() {
        boolean trainingPipelineStepFound = false;

        List<String> mlPipelines = this.getArtifactIds();

        if (!mlPipelines.isEmpty()) {
            for (Pipeline pipeline : pipelines) {
                BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
                List<BaseStepDecorator> pipelineSteps = pipelineDecorator.getSteps();
                // Potentially, if !pipelineSteps.isEmpty(), since current getSteps() implementation only returns
                // training steps
                for (Step step : pipelineSteps) {
                    if (step.getType().equalsIgnoreCase("training")) {
                        trainingPipelineStepFound = true;
                        break;
                    }
                }
            }
        }

        return trainingPipelineStepFound;
    }

    /**
     * Checks whether there are any pipeline steps that are inference or training type.
     *
     * @return true if a pipeline exists with a step that has inference or training
     */
    private boolean hasInferenceOrTrainingPipelineStep() {
        boolean inferenceOrTrainingPipelineStepFound = false;

        List<String> mlPipelines = this.getArtifactIds();

        if (!mlPipelines.isEmpty()) {
            for (Pipeline pipeline : pipelines) {
                BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
                List<BaseStepDecorator> pipelineSteps = pipelineDecorator.getSteps();
                // Potentially, if !pipelineSteps.isEmpty(), since current getSteps() implementation only returns
                // training steps
                for (Step step : pipelineSteps) {
                    if (step.getType().equalsIgnoreCase("inference") || step.getType().equalsIgnoreCase("training")) {
                        inferenceOrTrainingPipelineStepFound = true;
                        break;
                    }
                }
            }
        }

        return inferenceOrTrainingPipelineStepFound;
    }

    /**
     * Checks whether there is at least one pipeline with versioning enabled.
     * 
     * @return whether versioning support is needed
     */
    public boolean isVersioningSupportNeeded() {
        boolean enabled = false;

        // check if there's at least one pipeline with versioning enabled
        for (Pipeline pipeline : getPipelines()) {
            Versioning versioning = pipeline.getType().getVersioning();
            // versioning is not enabled by default
            if (versioning != null && versioning.isEnabled()) {
                enabled = true;
                break;
            }
        }

        return enabled;
    }

    /**
     * This method determines if postgres is needed. Currently postgres is needed if mlflow is needed.
     *
     * @return whether postgres support is needed
     */
    public boolean isPostgresSupportNeeded() {
        return isMlflowNeeded();
    }

    /**
     * This method determines if rdbms is needed. Currently rdbms is needed if mlflow is needed.
     *
     * @return whether rdbms support is needed
     */
    public boolean isRdbmsSupportNeeded() {
        return isMlflowNeeded();
    }

    private List<PipelineStepPair> getStepsByType(String type) {
        List<PipelineStepPair> steps = new ArrayList<>();

        for (Pipeline pipeline : pipelines) {
            PythonPipeline pythonPipeline = new PythonPipeline(pipeline);
            for (Step step : pythonPipeline.getSteps()) {
                if (type.equals(step.getType())) {
                    steps.add(new PipelineStepPair(pipeline, step));
                }
            }
        }

        return steps;
    }
}
