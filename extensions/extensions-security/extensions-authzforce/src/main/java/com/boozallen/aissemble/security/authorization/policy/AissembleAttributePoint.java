package com.boozallen.aissemble.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
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

import java.util.Collection;

/**
 * The interface that defines the contract for looking up attribute values. This can be a local or remote source. It
 * should be specified in the aissemble attribute definition json file for each attribute so that
 * {@link AissembleAttributeProvider} can find the value for a specified attribute.
 * 
 * While any number of attributes can be used for lookups, almost all scenarios will revolve around look up
 * attributes for specific subjects. As such, the interface will focus on that until a demand signal arises for more
 * complicated scenarios.
 * 
 * Implementations MUST have a no-argument constructor.
 */
public interface AissembleAttributePoint {

    /**
     * Returns the valid for a specific attribute id. For our purposes, we will just use id along to determine the value
     * and not worry about category, etc.
     * 
     * @param attributeId
     *            attribute id
     * @param subject
     *            the subject for which to find the attribute
     * @return The attribute's value
     */
    Collection<AttributeValue<?>> getValueForAttribute(String attributeId, String subject);

}
