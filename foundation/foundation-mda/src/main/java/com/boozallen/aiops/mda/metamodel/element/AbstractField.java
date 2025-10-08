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
 * Defines the contract for a generic field.
 */
public interface AbstractField extends Validatable {

    /**
     * Returns the name of the field.
     * 
     * @return name
     */
    String getName();

    /**
     * Returns the column of the field.
     * 
     * @return column
     */
    String getColumn();

    /**
     * Returns whether or not the field is required.
     * 
     * @return required
     */
    Boolean isRequired();

    /**
     * Returns the ethics policy for this type. This should be a URN to an applicable policy in the Ethics/Bias module.
     * This value will override a dictionary type setting, if appropriate.
     * 
     * @return ethics policy urn
     */
    String getEthicsPolicy();

    /**
     * Returns the drift policy for this type. This should be a URN to an applicable policy in the Drift module. This
     * value will override a dictionary type setting, if appropriate.
     * 
     * @return drift policy urn
     */
    String getDriftPolicy();
}
