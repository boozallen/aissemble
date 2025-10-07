package com.boozallen.aiops.mda.metamodel.element.java;

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

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataCollectionTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionType;

/**
 * Decorates StepDataCollectionType with Java-specific functionality.
 */
public class JavaStepDataCollectionType extends BaseStepDataCollectionTypeDecorator {

    /**
     * {@inheritDoc}
     */
    public JavaStepDataCollectionType(StepDataCollectionType stepDataCollectionTypeToDecorate) {
        super(stepDataCollectionTypeToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new JavaDictionaryType(super.getDictionaryType());
    }

    /**
     * Returns the short type of this step data collection type.
     * 
     * @return short type
     */
    public String getShortType() {
        JavaDictionaryType dictionaryType = (JavaDictionaryType) getDictionaryType();
        return dictionaryType.getShortType();
    }

    /**
     * Returns the fully qualified type of this step data collection type.
     * 
     * @return fully qualified type
     */
    public String getFullyQualifiedType() {
        JavaDictionaryType dictionaryType = (JavaDictionaryType) getDictionaryType();
        return dictionaryType.getFullyQualifiedType();
    }

}
