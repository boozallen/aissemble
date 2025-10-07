package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class PipelineMessagingResourcesGenerator extends AbstractResourcesGenerator {
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

		if (shouldGenerate(targetPipeline)) {
			generateFile(generationContext, vc);
		}
	}

	protected boolean shouldGenerate(BasePipelineDecorator pipeline) {
		return pipeline.isMessagingSupportNeeded();
	}
}
