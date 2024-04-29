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

/**
 * A library-egnostic view of possible policy decision values.  These values map directly to the XACML 3.0 standard.
 */
public enum PolicyDecision {

    PERMIT,
    DENY,
    NOT_APPLICABLE,
    INDETERMINATE;
    
}
