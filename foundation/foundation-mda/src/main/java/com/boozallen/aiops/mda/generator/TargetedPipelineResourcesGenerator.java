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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aissemble.common.Constants;

/**
 * Generates configuration code with no model interaction. This is often useful for
 * configuration files that must exist in some form or similar constructs.
 */
public class TargetedPipelineResourcesGenerator extends AbstractResourcesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                        | Template                                        | Generated File                                                      |
     * |-------------------------------|-------------------------------------------------|---------------------------------------------------------------------|
     * | pipelineTestConfig            | cucumber.test.pipeline-messaging.properties.vm  | krausening/test/pipeline-messaging.properties                       |
     * | microprofileConfigProperties  | pipeline.microprofile-config.properties.vm      | META-INF/microprofile-config.properties                             |
     * | microprofileConfigServices    | pipeline.services.microprofile.config.vm        | META-INF/services/org.eclipse.microprofile.config.spi.ConfigSource  |
     */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void generate(GenerationContext generationContext) {
		VelocityContext vc = getNewVelocityContext(generationContext);

		Pipeline pipeline = PipelineUtils.getTargetedPipeline(generationContext, metadataContext);
		BasePipelineDecorator targetPipeline = new BasePipelineDecorator(pipeline);
		vc.put(VelocityProperty.PIPELINE, targetPipeline);
		vc.put(VelocityProperty.DATA_LINEAGE_CHANNEL_NAME, Constants.DATA_LINEAGE_CHANNEL_NAME);
		vc.put(VelocityProperty.BASE_PACKAGE, generationContext.getBasePackage());

		String baseOutputFile = generationContext.getOutputFile();
		String fileName = replace("pipelineName", baseOutputFile, targetPipeline.getName());
		generationContext.setOutputFile(fileName);

		if (shouldGenerate(pipeline)) {
			generateFile(generationContext, vc);
		}
	}

	protected boolean shouldGenerate(Pipeline pipeline) {
		return true;
	}
}
