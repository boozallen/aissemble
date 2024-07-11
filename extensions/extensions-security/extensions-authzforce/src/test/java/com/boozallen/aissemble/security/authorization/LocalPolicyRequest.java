package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.security.authorization.policy.PolicyRequest;

public class LocalPolicyRequest extends PolicyRequest {
    public LocalPolicyRequest() {
    }

    public LocalPolicyRequest(String name, String resource, String action) {
        super(name, resource, action);
    }
}

