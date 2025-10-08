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
 * Represents a step data record type instance.
 */
@JsonPropertyOrder({ "name", "package" })
public class StepDataRecordTypeElement extends NamespacedMetamodelElement implements StepDataRecordType {

    private AissembleModelInstanceRepository modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AissembleModelInstanceRepository.class);

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public Record getRecordType() {
        Record record;
        if (StringUtils.isNotBlank(getPackage())) {
            record = modelRepository.getRecord(getPackage(), getName());
        } else {
            record = modelRepository.getRecord(getName());
        }

        return record;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if (getRecordType() == null) {
            messageTracker.addErrorMessage("Invalid record type - no record found! (package:'" + getPackage()
                    + "', name:'" + getName() + "')");
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
