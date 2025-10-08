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

import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;

/**
 * Defines the contract for how a step will persist.
 */
public interface Persist extends Validatable {

    /**
     * Returns the persistence type of this step.
     * 
     * @return persistence type
     */
    String getType();

    /**
     * Returns the persistence mode of this step.
     * 
     * @return persistence mode
     */
    String getMode();

    /**
     * This value allows for customization of the collection type (e.g., dataset). The value will be looked up in
     * type.json and translated.
     * 
     * @return collection type
     */
    StepDataCollectionType getCollectionType();

    /**
     * The record type that is being passed. The record will be looked up in the metamodel repository and translated.
     * 
     * @return record type
     */
    StepDataRecordType getRecordType();

}
