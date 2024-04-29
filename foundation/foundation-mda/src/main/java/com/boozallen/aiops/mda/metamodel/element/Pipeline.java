package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for a pipeline that supports Data Delivery or Machine
 * Learning.
 */
public interface Pipeline extends NamespacedMetamodel {

	/**
	 * Returns the description of this pipeline.
	 * 
	 * @return pipeline description
	 */
	String getDescription();

	/**
	 * Returns the type of pipeline represented by this metamodel.
	 * 
	 * @return pipeline type
	 */
	PipelineType getType();

	/**
	 * Returns the file stores used by this pipeline.
	 *
	 * @return file stores
	 */
	List<FileStore> getFileStores();

	/**
	 * Returns the steps contained in this pipeline.
	 * 
	 * @return steps
	 */
	List<? extends Step> getSteps();

	/**
	 * Returns true if any step in pipeline has data profiling enabled.
	 *
	 * @return
	 */
	boolean isDataProfilingEnabled();
	
	/**
	 * Returns true if this pipeline has airflow listed as an executionHelper
	 *
	 * @return
	 */
	boolean requiresAirflow();

	/**
	 * Returns true if the pipeline has Data lineage recording enabled. Defaults to false.
	 *
	 * @return
	 */
	boolean getDataLineage();

	/**
	 * Loops over all pipelines until a pipelines is found that tests true with predicate.
	 * Otherwise, returns false.
	 *
	 * @param pipelines
	 * @param predicate
	 * @return
	 */
	static boolean aPipelineExistsWhere(
			Collection<Pipeline> pipelines, Function<Pipeline, Boolean> predicate) {

		boolean result = false;

		for (Pipeline pipeline : pipelines) {
			result = predicate.apply(pipeline);

			if (result) {
				break;
			}
		}

		return result;
	}

}
