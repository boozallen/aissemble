package com.boozallen.aissemble.security;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Policy Decision Point Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.policy.AiopsAttributePoint;
import com.boozallen.aiops.security.authorization.policy.AttributeValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Place holder attribute point that just does some local logic to support authorization demonstration.
 * This can be replaced with an actual attribute provider.
 */
public class LocalAttributePoint implements AiopsAttributePoint {

    @Override
    public Collection<AttributeValue<?>> getValueForAttribute(String attributeId, String subject) {
        Collection<AttributeValue<?>> values = null;
        if ("urn:aiops:accessData".equals(attributeId)) {
            values = sourceDataAccess(attributeId, subject);
        }

        return values;
    }

    protected Collection<AttributeValue<?>> sourceDataAccess(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("aiops".equals(subject)) {
            value = new AttributeValue<Boolean>(attributeId, true);
        } else {
            value = new AttributeValue<Boolean>(attributeId, false);
        }

        return wrapSingleValueInCollection(value);
    }

    private Collection<AttributeValue<?>> wrapSingleValueInCollection(AttributeValue<?> value) {
        Collection<AttributeValue<?>> values = new ArrayList<>();
        values.add(value);
        return values;
    }
}

