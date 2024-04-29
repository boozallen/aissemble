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

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides baseline decorator functionality for {@link Record}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseRecordDecorator implements Record {

    protected Record wrapped;

    /**
     * New decorator for {@link Record}.
     * 
     * @param recordToDecorate
     *            instance to decorate
     */
    public BaseRecordDecorator(Record recordToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), recordToDecorate);
        wrapped = recordToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return wrapped.getPackage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return wrapped.getTitle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return wrapped.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        wrapped.validate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataAccess getDataAccess() {
        return wrapped.getDataAccess();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RecordField> getFields() {
        return wrapped.getFields();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Framework> getFrameworks() {
        return wrapped.getFrameworks();
    }

    /**
     * Returns the record name, capitalized.
     * 
     * @return capitalized name
     */
    public String getCapitalizedName() {
        return StringUtils.capitalize(getName());
    }

    /**
     * Returns the record name in lower camel case format.
     * 
     * @return lower camel case name
     */
    public String getLowerCamelCaseName() {
        return PipelineUtils.deriveLowerCamelNameFromUpperCamelName(getName());
    }

    /**
     * Checks if this record has at least one field with validations.
     * 
     * @return true if this record has at least one field with validations
     */
    public boolean hasFieldValidations() {
        boolean hasFieldValidations = false;

        for (RecordField field : getFields()) {
            BaseRecordFieldDecorator baseField = (BaseRecordFieldDecorator) field;
            if (baseField.isRequired() || baseField.hasValidation()) {
                hasFieldValidations = true;
                break;
            }
        }

        return hasFieldValidations;
    }

    /**
     * Get a JSON representation of the record's fields
     * @return a JSON representation of the record's fields
     * @throws JsonProcessingException
     */
    public String getSchema() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final List<SchemaField> fieldSchemas = getFields()
                .stream()
                .map(rf -> new SchemaField((BaseRecordFieldDecorator) rf))
                .collect(Collectors.toList());
        return mapper.writeValueAsString(fieldSchemas);
    }

}
