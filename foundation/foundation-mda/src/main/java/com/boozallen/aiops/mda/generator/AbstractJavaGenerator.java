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

import com.boozallen.aiops.mda.generator.common.AbstractGeneratorAissemble;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Common configuration for generating Java classes.
 */
public abstract class AbstractJavaGenerator extends AbstractGeneratorAissemble {

	@Override
	protected VelocityContext getNewVelocityContext(GenerationContext generationContext) {
		VelocityContext vc = super.getNewVelocityContext(generationContext);
		vc.put(VelocityProperty.BASE_PACKAGE, generationContext.getBasePackage());

		return vc;

	}

	@Override
	public void generate(GenerationContext generationContext) {
		setOutputFileName(generationContext);

	}

	protected void setOutputFileName(GenerationContext generationContext) {
		String basefileName = generationContext.getOutputFile();
		basefileName = replaceBasePackage(basefileName, generationContext.getBasePackageAsPath());
		generationContext.setOutputFile(basefileName);

	}

	@Override
	protected String getOutputSubFolder() {
		return "java/";
	}

}
