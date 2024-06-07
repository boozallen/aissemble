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
 * Implements the {@link RecordFieldType} interface to support dictionary or composite field types for records.
 */
@JsonPropertyOrder({ "name", "package" })
public class RecordFieldTypeElement extends NamespacedMetamodelElement implements RecordFieldType {

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isDictionaryTyped() {
        return getDictionaryType() != null;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isCompositeTyped() {
        return getCompositeType() != null;
    }

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
    @JsonIgnore
    @Override
    public Composite getCompositeType() {
        Composite composite;
        if (StringUtils.isNotBlank(getPackage())) {
            composite = modelRepository.getComposite(getPackage(), getName());
        } else {
            composite = modelRepository.getComposite(getName());
        }

        return composite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        if (!isDictionaryTyped() && !isCompositeTyped()) {
            messageTracker.addErrorMessage(
                    "Invalid record field type - neither dictionary type nor composite! (package:'" + getPackage() + "', name:'" + getName() + "')");
        }
    }

    /**
     * Part of the record schema.
     * 
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return null;
    }

}
