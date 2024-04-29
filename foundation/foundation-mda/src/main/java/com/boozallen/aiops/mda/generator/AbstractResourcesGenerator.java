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

/**
 * Common configuration for generating Java classes.
 */
public abstract class AbstractResourcesGenerator extends AbstractGeneratorAissemble {
	@Override
	protected String getOutputSubFolder() {
		return "resources/";
	}
}
