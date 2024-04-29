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
