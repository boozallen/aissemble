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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record instance.
 */
@JsonPropertyOrder({ "package", "name", "title", "description", "dataAccess", "featureRegistration", "fields", "frameworks" })
public class RecordElement extends NamespacedMetamodelElement implements Record {

    @JsonIgnore
    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    @JsonInclude(Include.NON_NULL)
    private String title;

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonInclude(Include.NON_NULL)
    private DataAccess dataAccess;

    @JsonInclude(Include.NON_NULL)
    private Object featureRegistration;

    @JsonInclude(Include.NON_NULL)
    private List<RecordField> fields = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private List<Framework> frameworks = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public String getTitle() {
        return StringUtils.isEmpty(title) ? name : title;
    }

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
    public DataAccess getDataAccess() {
        return dataAccess;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<RecordField> getFields() {
        return fields;
    }

    /**
     * Adds a field to this instance.
     * 
     * @param field
     *            field to add
     */
    public void addField(RecordField field) {
        this.fields.add(field);
    }


    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<Framework> getFrameworks() {
        return frameworks;
    }

    /**
     * Adds a framework to be supported.
     * @param framework the framework to add support for
     */
    public void addFramework(final Framework framework) {
        this.frameworks.add(framework);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        for (RecordField field : fields) {
            field.validate();
        }
        if (this.featureRegistration != null) {
            manualActionNotificationService.addSchemaElementDeprecationNotice("featureRegistration", "Record");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return "aiops-record-schema.json";
    }

    public void setTitle(String title) { this.title = title; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void setFeatureRegistration(Object featureRegistration) {
        this.featureRegistration = featureRegistration;
    }
}
