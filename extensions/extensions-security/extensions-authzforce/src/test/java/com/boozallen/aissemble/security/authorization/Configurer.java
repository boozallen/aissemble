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
import io.cucumber.java.DataTableType;

import java.util.Map;

public class Configurer {

    @DataTableType
    public static PolicyRequest policyRequestEntry(Map<String, String> entry) {
        return new PolicyRequest(entry.get("name"), entry.get("resource"), entry.get("action"));
    }

    @DataTableType
    public static TokenDataInput tokenDataInputEntry(Map<String, String> entry) {
        return new TokenDataInput(entry.get("name"), entry.get("resource"), entry.get("action"), entry.get("result"));
    }
}
