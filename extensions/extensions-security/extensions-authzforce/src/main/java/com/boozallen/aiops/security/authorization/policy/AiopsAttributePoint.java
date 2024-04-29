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

import java.util.Collection;

/**
 * The interface that defines the contract for looking up attribute values. This can be a local or remote source. It
 * should be specified in the aiops attribute definition json file for each attribute so that
 * {@link AiopsAttributeProvider} can find the value for a specified attribute.
 * 
 * While any number of attributes can be used for lookups, almost all scenarios will revolve around look up
 * attributes for specific subjects. As such, the interface will focus on that until a demand signal arises for more
 * complicated scenarios.
 * 
 * Implementations MUST have a no-argument constructor.
 */
public interface AiopsAttributePoint {

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
