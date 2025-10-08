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

import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;

/**
 * Defines the contract for how validation is configured for a type.
 */
public interface Validation extends Validatable {

    /**
     * @return Returns the maxLength.
     */
    Integer getMaxLength();

    /**
     * @return Returns the minLength.
     */
    Integer getMinLength();

    /**
     * @return Returns the maxValue.
     */
    String getMaxValue();

    /**
     * @return Returns the minValue.
     */
    String getMinValue();

    /**
     * Returns the desired scale of a decimal value.
     * 
     * @return scale
     */
    Integer getScale();

    /**
     * Returns any formats for this validation the form of regular expressions.
     * 
     * @return one or more regex values
     */
    Collection<String> getFormats();

}
