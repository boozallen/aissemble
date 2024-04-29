package com.boozallen.aiops.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ow2.authzforce.core.pdp.api.XmlUtils;
import org.ow2.authzforce.core.pdp.api.value.AnyUriValue;
import org.ow2.authzforce.core.pdp.api.value.BooleanValue;
import org.ow2.authzforce.core.pdp.api.value.DateValue;
import org.ow2.authzforce.core.pdp.api.value.DoubleValue;
import org.ow2.authzforce.core.pdp.api.value.IntegerValue;
import org.ow2.authzforce.core.pdp.api.value.MediumInteger;
import org.ow2.authzforce.core.pdp.api.value.StringValue;

/**
 * Holds an attribute value returned via a {@link AiopsAttributeProvider}.
 */
public class AttributeValue<T> {
    private String attributeId;
    private T value;

    public AttributeValue(String attributeId, T value) {
        this.attributeId = attributeId;
        this.value = value;
    }

    public String getAttributeId() {
        return attributeId;
    }

    public T getValue() {
        return value;
    }

    /**
     * Returns the value as a string if not null
     * 
     * @return string value
     */
    public String getValueAsString() {
        return (value != null) ? value.toString() : null;
    }

    public StringValue getAsStringValue() {
        return (value != null) ? new StringValue(value.toString()) : null;
    }

    public IntegerValue getAsIntegerValue() {
        Integer valueAsInteger = (Integer) value;
        return (valueAsInteger != null) ? new IntegerValue(new MediumInteger(valueAsInteger.intValue())) : null;
    }

    public DoubleValue getAsDoubleValue() {
        Double valueAsDouble = (Double) value;
        return (valueAsDouble != null) ? new DoubleValue(valueAsDouble) : null;
    }

    public AnyUriValue getAsAnyUriValue() {
        return (value != null) ? new AnyUriValue(value.toString()) : null;
    }

    public BooleanValue getAsBooleanValue() {
        Boolean valueAsBoolean = (Boolean) value;
        return (valueAsBoolean != null) ? new BooleanValue(valueAsBoolean) : null;
    }

    public DateValue getAsDateValue() {
        Date valueAsDate = (Date) value;
        GregorianCalendar valueAsGc = new GregorianCalendar();
        valueAsGc.setTime(valueAsDate);
        XMLGregorianCalendar xmlGc = XmlUtils.XML_TEMPORAL_DATATYPE_FACTORY.newXMLGregorianCalendar(valueAsGc);
        return (valueAsDate != null) ? DateValue.getInstance(xmlGc) : null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
