package com.boozallen.aissemble.security.client;

/*-
 * #%L
 * AIOps Foundation::AIOps Core Security::AIOps Policy Decision Point Client
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
