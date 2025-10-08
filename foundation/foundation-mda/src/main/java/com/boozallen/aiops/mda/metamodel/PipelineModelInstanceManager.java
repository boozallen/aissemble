package com.boozallen.aiops.mda.metamodel;

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

import org.technologybrewery.fermenter.mda.metamodel.AbstractMetamodelManager;

import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.PipelineElement;

/**
 * Responsible for maintaining the list of pipelines model instances elements in
 * the system.
 */
class PipelineModelInstanceManager extends AbstractMetamodelManager<Pipeline> {

	private static final ThreadLocal<PipelineModelInstanceManager> instance = ThreadLocal.withInitial(PipelineModelInstanceManager::new);

	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return singleton
	 */
	public static PipelineModelInstanceManager getInstance() {
		return instance.get();
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
