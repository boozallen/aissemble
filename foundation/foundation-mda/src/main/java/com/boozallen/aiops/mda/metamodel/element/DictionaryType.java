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

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodel;

/**
 * Defines the contract for an entry in the dictionary (e.g., validation, policies).
 */
public interface DictionaryType extends NamespacedMetamodel {

    /**
     * Returns the simple type of this dictionary type (e.g., string, integer).
     * 
     * @return simple type of dictionary type
     */
    String getSimpleType();

    /**
     * Returns any validation constraints associated with this type (e.g., length restrictions, regex).
     * 
     * @return validation constrains of the dictionary type
     */
    Validation getValidation();

    /**
     * Returns the ethics policy for this type. This should be a URN to an applicable policy in the Ethics/Bias module.
     * 
     * @return ethics policy urn
     */
    String getEthicsPolicy();

    /**
     * Returns the drift policy for this type. This should be a URN to an applicable policy in the Drift module.
     * 
     * @return drift policy urn
     */
    String getDriftPolicy();

}
