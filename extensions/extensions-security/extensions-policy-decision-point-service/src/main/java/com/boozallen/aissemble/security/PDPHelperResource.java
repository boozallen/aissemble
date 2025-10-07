package com.boozallen.aissemble.security;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Policy Decision Point Service
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
import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import com.boozallen.aissemble.security.authorization.AissembleSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.AissembleSimpleSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecisionPoint;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class PDPHelperResource {
    private AissembleSecureTokenServiceClient tokenClient = new AissembleSimpleSecureTokenServiceClient();
    private PolicyDecisionPoint pdp = PolicyDecisionPoint.getInstance();

    /**
     * This REST resource checks with the security policies and determines if the provided
     * identity is authorized to perform the requested action.
     * @param pdpRequest The request which includes a subject, resource and action
     * @return a policy decision
     */
    @POST
    @Path("/pdp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String getPdpDecision(PDPRequest pdpRequest) {
        Jws<Claims> jws = tokenClient.parseToken(pdpRequest.getJwt());
        Claims claims = jws.getBody();

        PolicyDecision policyDecision = pdp.isAuthorized(claims.getSubject(), pdpRequest.getResource(), pdpRequest.getAction());

        return policyDecision.toString();
    }

    /**
     * This method authenticates the provide user
     * @param authRequest the authentication request including a username and a password
     * @return a valid jwt token if the user is successfully authenticated
     */
    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String authenticate(AuthRequest authRequest) {
        AissembleSecureTokenServiceClient aissembleSecureTokenServiceClient = new AissembleSimpleSecureTokenServiceClient();
        String jwt = aissembleSecureTokenServiceClient.authenticate(authRequest.getUsername(), authRequest.getPassword());

        return jwt;
    }

    @GET
    @Path("/healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    public String healthCheck() {
        return "PDP Service is running...\n";
    }
}
