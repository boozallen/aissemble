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
 * Defines the contract for field types on a record (dictionary).
 */
public interface RecordFieldType extends NamespacedMetamodel {

    /**
     * Returns true if the field is sourced from a dictionary.
     * 
     * @return whether or not a dictionary type
     */
    boolean isDictionaryTyped();

    /**
     * Returns the dictionary entry for this type, only if it is a dictionary type.
     * 
     * @return dictionary entry
     */
    DictionaryType getDictionaryType();    

}
