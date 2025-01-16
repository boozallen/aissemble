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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

/**
 * Represents a reference on an record.
 */
@JsonPropertyOrder({ "type", "package", "multiplicity" })
public class RelationElement implements Relation {
    protected static final String PACKAGE = "package";

    @JsonIgnore
    private static MessageTracker messageTracker = MessageTracker.getInstance();

    @JsonInclude(Include.NON_NULL)
    @JsonProperty(value = PACKAGE)
    protected String packageName;

    @JsonInclude(Include.NON_NULL)
    protected String name;

    @JsonInclude(Include.NON_NULL)
    protected String documentation;

    @JsonInclude(Include.NON_NULL)
    protected Multiplicity multiplicity;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    public String getFileName() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

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


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        if (multiplicity == null) {
            multiplicity = Multiplicity.ONE_TO_MANY;
        }
    }

    /**
     * Sets the relation type package.
     * 
     * @param packageName
     *            relation type packageName
     */
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets the relation type.
     * 
     * @param name
     *            relation type
     */
    public void setName(String name) {
        this.name = name;
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

}
