package com.boozallen.aissemble.security;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Policy Decision Point Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aissemble.security.authorization.policy.AissembleAttributePoint;
import com.boozallen.aissemble.security.authorization.policy.AttributeValue;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Place holder attribute point that just does some local logic to support authorization demonstration.
 * This can be replaced with an actual attribute provider.
 */
public class LocalAttributePoint implements AissembleAttributePoint {

    @Override
    public Collection<AttributeValue<?>> getValueForAttribute(String attributeId, String subject) {
        Collection<AttributeValue<?>> values = null;
        if ("urn:aissemble:accessData".equals(attributeId)) {
            values = sourceDataAccess(attributeId, subject);
        }

        return values;
    }

    protected Collection<AttributeValue<?>> sourceDataAccess(String attributeId, String subject) {
        AttributeValue<?> value = null;

        if ("aissemble".equals(subject)) {
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

