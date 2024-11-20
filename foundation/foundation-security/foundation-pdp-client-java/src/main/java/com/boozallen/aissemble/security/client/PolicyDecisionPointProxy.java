package com.boozallen.aissemble.security.client;

/*-
 * #%L
 * AIOps Foundation::AIOps Core Security::AIOps Policy Decision Point Client
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.security.authorization.models.AuthRequest;
import com.boozallen.aissemble.security.authorization.models.PDPRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

public interface PolicyDecisionPointProxy {
    @POST
    @Path("/api/pdp")
    @Consumes("application/json")
    @Produces("text/plain")
    String getDecision(PDPRequest policyDecisionRequest);

    @POST
    @Path("/api/authenticate")
    @Consumes("application/json")
    @Produces("text/plain")
    String authenticate(AuthRequest authRequest);
}
