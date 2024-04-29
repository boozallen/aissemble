package com.boozallen.aiops.mda.metamodel.element;

import java.util.List;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.fermenter.mda.metamodel.element.Metamodel;

/**
 * Defines the contract for a pipeline type that determines the type of AIOps
 * pipeline this represents along with the target implementation type.
 */
public interface PipelineType extends Metamodel {

	/**
	 * Returns the implementation type of this pipeline.
	 * 
	 * @return implementation type description
	 */
	String getImplementation();
	
    /**
     * Returns the versioning settings of this pipeline.
     *
     * @return versioning settings
     */
    Versioning getVersioning();

    /**
     * Returns the platforms for this pipeline.
     * 
     * @return platforms
     */
    List<Platform> getPlatforms();

	/**
	 * Returns the execution helper list for this pipeline.
	 *
	 * @return execution helper list
	 */
	List<String> getExecutionHelpers();

}
