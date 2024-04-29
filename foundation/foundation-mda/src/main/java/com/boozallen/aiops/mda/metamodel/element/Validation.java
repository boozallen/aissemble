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

import java.util.Collection;

import org.technologybrewery.fermenter.mda.metamodel.element.Validatable;

/**
 * Defines the contract for how validation is configured for a type.
 */
public interface Validation extends Validatable {

    /**
     * @return Returns the maxLength.
     */
    Integer getMaxLength();

    /**
     * @return Returns the minLength.
     */
    Integer getMinLength();

    /**
     * @return Returns the maxValue.
     */
    String getMaxValue();

    /**
     * @return Returns the minValue.
     */
    String getMinValue();

    /**
     * Returns the desired scale of a decimal value.
     * 
     * @return scale
     */
    Integer getScale();

    /**
     * Returns any formats for this validation the form of regular expressions.
     * 
     * @return one or more regex values
     */
    Collection<String> getFormats();

}
