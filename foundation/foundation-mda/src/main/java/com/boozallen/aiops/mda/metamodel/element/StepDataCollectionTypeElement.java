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

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a step data collection type instance.
 */
@JsonPropertyOrder({ "name", "package" })
public class StepDataCollectionTypeElement extends NamespacedMetamodelElement implements StepDataCollectionType {

    private AissembleModelInstanceRepository modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AissembleModelInstanceRepository.class);

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public DictionaryType getDictionaryType() {
        DictionaryType dictionaryType;
        if (StringUtils.isNotBlank(getPackage())) {
            dictionaryType = modelRepository.getDictionaryType(getPackage(), getName());
        } else {
            dictionaryType = modelRepository.getDictionaryType(getName());
        }

        return dictionaryType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if (getDictionaryType() == null) {
            messageTracker.addErrorMessage("Invalid collection type - no dictionary type found! (package:'"
                    + getPackage() + "', name:'" + getName() + "')");
        }
    }

    /**
     * Part of the pipeline schema.
     * 
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return null;
    }

}
