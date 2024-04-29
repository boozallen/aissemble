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

import java.math.BigDecimal;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a enumeration of declared constants.
 */
@JsonPropertyOrder({ "package", "name", "maxLength", "minLength", "maxValue", "minValue", "scale", "formats" })
public class ValidationElement implements Validation {

    protected static MessageTracker messageTracker = MessageTracker.getInstance();

    @JsonInclude(Include.NON_NULL)
    private Integer maxLength;

    @JsonInclude(Include.NON_NULL)
    private Integer minLength;

    /**
     * Applies to both integers and floating point values, so use a String to store.
     */
    @JsonInclude(Include.NON_NULL)
    private String maxValue;

    /**
     * Applies to both integers and floating point values, so use a String to store.
     */
    @JsonInclude(Include.NON_NULL)
    private String minValue;

    @JsonInclude(Include.NON_NULL)
    private Integer scale;

    @JsonInclude(Include.NON_NULL)
    private Collection<String> formats;

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public Integer getMaxLength() {
        return maxLength;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public Integer getMinLength() {
        return minLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public String getMinValue() {
        return minValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public Integer getScale() {
        return scale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonInclude(Include.NON_NULL)
    public Collection<String> getFormats() {
        return formats;
    }

    /**
     * Sets the maximum length of a string.
     * 
     * @param maxLength
     *            string max length
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Sets the minimum length of a string.
     * 
     * @param minLength
     *            string min length
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Sets the maximum value of a numeric type.
     * 
     * @param maxValue
     *            numeric max value
     */
    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Sets the minimum value of a numeric type.
     * 
     * @param minValue
     *            numeric min value
     */
    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    /**
     * Sets scale (number of places to right of decimal point).
     * 
     * @param scale
     *            allowed places
     */
    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * Format name for regular expression matching.
     * 
     * @param formats
     *            format name (reference to {@link Format}).
     */
    public void setFormats(Collection<String> formats) {
        this.formats = formats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        validateLengths();
        validateRange();
        validateFormats();
        validateScale();
    }

    private void validateLengths() {
        if (minLength != null && minLength < 0) {
            messageTracker.addErrorMessage("minLength '" + minLength + "' cannot be less than zero!");
        }

        if (maxLength != null && maxLength < 0) {
            messageTracker.addErrorMessage("maxLength '" + maxLength + "' cannot be less than zero!");
        }

        if (maxLength != null && minLength != null && minLength.compareTo(maxLength) > 0) {
            messageTracker.addErrorMessage("minLength '" + minLength + "' exceeds maxLength '" + maxLength + "'!");
        }
    }

    private void validateRange() {
        if (maxValue != null && minValue != null) {
            BigDecimal numericMinValue = new BigDecimal(minValue);
            BigDecimal numericMaxValue = new BigDecimal(maxValue);

            if (numericMinValue.compareTo(numericMaxValue) > 0) {
                messageTracker.addErrorMessage(
                        "minValue '" + numericMinValue + "' exceeds maxValue '" + numericMaxValue + "'!");
            }
        }
    }

    private void validateFormats() {
        // remove any empty formats:
        if (CollectionUtils.isNotEmpty(formats)) {
            boolean foundEmptyFormats = false;
            Collection<String> trimmedFormats = new ArrayList<>();
            for (String format : formats) {
                if (StringUtils.isNotBlank(format)) {
                    trimmedFormats.add(format.trim());
                    foundEmptyFormats = true;
                }
            }

            this.formats = trimmedFormats;

            if (foundEmptyFormats) {
                messageTracker.addWarningMessage(
                        "At least one empty format was stripped - please remove from your Validation definition!");
            }

        }
    }

    private void validateScale() {
        if (scale != null && scale < 0) {
            messageTracker.addErrorMessage("scale '" + scale + "' cannot be less than zero!");
        }
    } 

    /**
     * Determines if any validation constraint information has been configured.
     * 
     * @return true if configured
     */
    public boolean hasValidationContraints() {
        return maxLength != null || minLength != null || maxValue != null || minValue != null || scale != null
                || formats != null;
    }

}
