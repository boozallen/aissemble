package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.AbstractMetamodelManager;

import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PipelineElement;

/**
 * Responsible for maintaining the list of pipelines model instances elements in
 * the system.
 */
class PipelineModelInstanceManager extends AbstractMetamodelManager<Pipeline> {

	private static final PipelineModelInstanceManager instance = new PipelineModelInstanceManager();

	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return singleton
	 */
	public static PipelineModelInstanceManager getInstance() {
		return instance;
	}

	/**
	 * Prevent instantiation of this singleton from outside this class.
	 */
	private PipelineModelInstanceManager() {
		super();
	}

	@Override
	protected String getMetadataLocation() {
		return "pipelines";
	}

	@Override
	protected Class<PipelineElement> getMetamodelClass() {
		return PipelineElement.class;
	}

	@Override
	protected String getMetamodelDescription() {
		return Pipeline.class.getSimpleName();
	}
	
}
