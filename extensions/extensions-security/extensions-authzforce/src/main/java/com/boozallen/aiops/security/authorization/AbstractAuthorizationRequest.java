package com.boozallen.aiops.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.policy.ClaimType;

/**
 * Common aspect of a request for authorization information (e.g., a policy decision, attribute).
 *
 */
public abstract class AbstractAuthorizationRequest {

    /**
     * Returns the type of claim represented by this instance.
     *
     * @return claim type
     */
    protected abstract ClaimType getClaimType();
}
