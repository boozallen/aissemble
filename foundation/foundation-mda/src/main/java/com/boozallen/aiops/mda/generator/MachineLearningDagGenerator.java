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

import com.boozallen.aiops.mda.generator.common.PipelineContext;
import com.boozallen.aiops.mda.generator.common.MachineLearningStrategy;
import com.boozallen.aiops.mda.generator.common.PipelineStepPair;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;
import com.boozallen.aiops.mda.metamodel.element.python.PythonStep;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Iterates through each pipeline in the metamodel and enables the generation of
 * a single DAG file for each machine learning training step.
 */
public class MachineLearningDagGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                          | Template                                            | Generated File                   |
     * |---------------------------------|-----------------------------------------------------|----------------------------------|
     * | airflowMachineLearningDagFiles  | deployment/airflow/airflow.machine.learning.dag.vm  | dags/${pipelineStepName}_dag.py  |
     */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(GenerationContext generationContext) {
		 AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
	                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

		Map<String, Pipeline> pipelineMap = metamodelRepository.getPipelinesByContext(metadataContext);

		String baseOutputFile = generationContext.getOutputFile();
		List<Pipeline> pipelines = new ArrayList<>(pipelineMap.values());

		//Gets the training steps and associated pipeline for machine learning pipelines
		PipelineContext pipelineContext = new PipelineContext();
		pipelineContext.setPipelineStrategy(new MachineLearningStrategy(pipelines));
		List<PipelineStepPair> machineLearningSteps = pipelineContext.getSteps();

		for (PipelineStepPair pipelineStepPair : machineLearningSteps) {
			PythonStep pythonStep = new PythonStep(pipelineStepPair.getStep());
			PythonPipeline pythonPipeline = new PythonPipeline(pipelineStepPair.getPipeline());

			VelocityContext vc = getNewVelocityContext(generationContext);
			vc.put(VelocityProperty.PYTHON_PIPELINE, pythonPipeline);
			vc.put(VelocityProperty.PIPELINE_STEP, pythonStep);
			String fileName = replace("pipelineStepName", baseOutputFile, pythonStep.getLowercaseSnakeCaseName());
			generationContext.setOutputFile(fileName);

			generateFile(generationContext, vc);
		}
	}
}
