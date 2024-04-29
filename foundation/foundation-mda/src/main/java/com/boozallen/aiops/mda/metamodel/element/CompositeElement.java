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
 * Represents a composite instance.
 */
@JsonPropertyOrder({ "package", "name", "description", "fields" })
public class CompositeElement extends NamespacedMetamodelElement implements Composite {

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonInclude(Include.NON_NULL)
    private List<CompositeField> fields = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<CompositeField> getFields() {
        return fields;
    }

    /**
     * Adds a field to this instance.
     * 
     * @param field
     *            field to add
     */
    public void addField(CompositeField field) {
        this.fields.add(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        for (CompositeField field : fields) {
            field.validate();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return "aiops-composite-schema.json";
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
