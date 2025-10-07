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

import java.util.List;

import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItem;
import org.technologybrewery.fermenter.mda.metamodel.element.Metamodel;

/**
 * Defines the contract for a pipeline step.
 */
public interface Step extends Metamodel {

	/**
	 * Returns the type of this pipeline (e.g., enrich, transform).
	 * 
	 * @return type of pipeline step
	 */
	String getType();

	/**
	 * Returns the inbound data binding for this step.
	 * 
	 * @return inbound data binding
	 */
	StepDataBinding getInbound();

	/**
	 * Returns the outbound data binding for this step.
	 * 
	 * @return outbound data binding
	 */
	StepDataBinding getOutbound();

	/**
	 * Returns the persist settings this step.
	 * 
	 * @return persist settings
	 */
	Persist getPersist();

	/**
	 * Returns the provenance settings this step.
	 *
	 * @return provenance settings
	 */
	Provenance getProvenance();
	
	/**
	 * Returns if the alert is enabled on this step.
	 * 
	 * @return
	 */
	Alerting getAlerting();

    /**
     * Returns the data profiling settings for this step.
     * 
     * @return data profiling
     */
    DataProfiling getDataProfiling();

	/**
     * Returns the model lineage settings for this step.
     * 
     * @return model lineage
     */
	ModelLineage getModelLineage();

	/**
	 * Returns true if data profiling is enabled.
	 * @return
	 */
	boolean isDataProfilingEnabled();

    /**
     * Returns the post-actions for this step.
     * 
     * @return list of post-actions
     */
    List<PostAction> getPostActions();

	/**
	 * Returns any key/value pairs associated with this step.
	 * 
	 * @return list of key value pairs
	 */
	List<ConfigurationItem> getConfiguration();

	/**
	 * A list of file stores
	 * @return list of file stores
	 */
	List<String> getFileStores();
}
