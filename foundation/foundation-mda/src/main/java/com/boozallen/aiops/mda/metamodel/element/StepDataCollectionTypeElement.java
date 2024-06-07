package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
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

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

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
