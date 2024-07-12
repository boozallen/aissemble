package com.boozallen.aissemble.security.authorization.policy;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.security.authorization.AbstractAuthorizationRequest;

/**
 * Represents a request for an attribute value to be returned for a claim.
 */
public class AttributeRequest extends AbstractAuthorizationRequest {
    private String requestedAttributeId;
    
    public AttributeRequest(String requestedAttributeId) {
        this.requestedAttributeId = requestedAttributeId;
    }
    
    public String getRequestedAttributeId() {
        return this.requestedAttributeId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ClaimType getClaimType() {
        return ClaimType.ATTRIBUTE;
    }
}
