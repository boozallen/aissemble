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

import com.boozallen.aiops.security.authorization.AbstractAuthorizationRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents a resource and action pair for which to request a policy decision. Typically, this will include the
 * following: * A simple name by which to represent the policy rule or claim name * A representative resource/action
 * pair that creates a decision pursuant to that rule/claim
 */
public class PolicyRequest extends AbstractAuthorizationRequest {
    public static final String ANY = "";

    protected String name;
    protected String resource;
    protected String action;

    public PolicyRequest() {
        // default constructor
    }

    public PolicyRequest(String name, String resource, String action) {
        this.name = name;
        this.resource = resource;
        this.action = action;
    }

    /**
     * Returns the name or blends the resource and action to create a name, if none is found.
     * 
     * @return name of this attribute
     */
    public String getName() {
        String localName = this.name;
        if (StringUtils.isBlank(localName)) {
            localName = getResource() + "^" + getAction();
        }
        return localName;
    }

    public void setName(String name) {
        this.name = name;
    } 

    public String getResource() {
        return StringUtils.isNotBlank(resource) ? resource : ANY;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return StringUtils.isNotBlank(action) ? action : ANY;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }       
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ClaimType getClaimType() {
        return ClaimType.POLICY;
    }
}
