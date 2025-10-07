package com.boozallen.aiops.mda.metamodel.element;

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
