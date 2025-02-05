package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

/**
 * Represents a reference on a record.
 */
@JsonPropertyOrder({ "name", "package", "multiplicity", "column", "required" })
public class RelationElement extends NamespacedMetamodelElement implements Relation {

    @JsonIgnore
    private static final MessageTracker messageTracker = MessageTracker.getInstance();

    @JsonInclude(Include.NON_NULL)
    protected String documentation;

    @JsonInclude(Include.NON_NULL)
    protected Multiplicity multiplicity;

    @JsonInclude(Include.NON_NULL)
    protected Boolean required;

    @JsonInclude(Include.NON_NULL)
    protected String column;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDocumentation() {
        return documentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    @Override
    public Boolean isRequired() {
        return required;
    }

    @Override
    public String getColumn() {
        return column;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if (multiplicity == null) {
            multiplicity = Multiplicity.ONE_TO_MANY;
        }
        super.validate();
        //TODO: this can be removed when upgrade to fermenter 2.10.6
        if (getPackage() == null) {
            messageTracker.addErrorMessage("Package is a required attribute!");
        }
    }

    /**
     * Sets the documentation value.
     * 
     * @param documentation
     *            documentation text
     */
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /***
     * Sets the Column name.
     * @param column the column name
     */
    public void setColumn(String column) {
        // This method is used in testing. i.e. not normally set from the RelationElement directly
        this.column = column;
    }

    /**
     * Sets the multiplicity value.
     * 
     * @param multiplicityAsString
     *            multiplicity value
     */
    public void setMultiplicity(String multiplicityAsString) {
        this.multiplicity = Multiplicity.fromString(multiplicityAsString);

        if (StringUtils.isNotBlank(multiplicityAsString) && multiplicity == null) {
            messageTracker.addErrorMessage("Could not map multiplicity '" + multiplicityAsString
                    + "' to one of the known multiplicity types! (" + Multiplicity.options() + ") ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("name", name).toString();
    }

    @Override
    public String getSchemaFileName() {
        return null;
    }
}
