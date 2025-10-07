package com.boozallen.aiops.mda.metamodel.element;

import java.util.List;

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
