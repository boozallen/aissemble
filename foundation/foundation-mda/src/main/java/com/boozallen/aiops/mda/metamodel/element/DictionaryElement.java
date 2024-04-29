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

import java.util.ArrayList;
import java.util.List;

import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a dictionary instance.
 */
@JsonPropertyOrder({ "package", "name" })
public class DictionaryElement extends NamespacedMetamodelElement implements Dictionary {

    @JsonInclude(Include.NON_NULL)
    private List<DictionaryType> dictionaryTypes = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<DictionaryType> getDictionaryTypes() {
        return dictionaryTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return "aiops-dictionary-schema.json";
    }

    /**
     * Adds a type to this dictionary.
     * 
     * @param dictionaryType
     *            type to add
     */
    public void addDictionaryType(DictionaryType dictionaryType) {
        this.dictionaryTypes.add(dictionaryType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        super.validate();

        for (DictionaryType dictionaryType : dictionaryTypes) {
            dictionaryType.validate();
        }

    }

}
