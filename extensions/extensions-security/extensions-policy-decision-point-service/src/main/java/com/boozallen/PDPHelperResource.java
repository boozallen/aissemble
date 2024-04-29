package com.boozallen;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Policy Decision Point Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.models.AuthRequest;
import com.boozallen.aiops.security.authorization.models.PDPRequest;
import com.boozallen.aiops.security.authorization.policy.PolicyDecision;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import com.boozallen.aiops.security.authorization.AiopsSecureTokenServiceClient;
import com.boozallen.aiops.security.authorization.AiopsSimpleSecureTokenServiceClient;
import com.boozallen.aiops.security.authorization.policy.PolicyDecisionPoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class PDPHelperResource {
    private AiopsSecureTokenServiceClient tokenClient = new AiopsSimpleSecureTokenServiceClient();
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
        AiopsSecureTokenServiceClient aiopsSecureTokenServiceClient = new AiopsSimpleSecureTokenServiceClient();
        String jwt = aiopsSecureTokenServiceClient.authenticate(authRequest.getUsername(), authRequest.getPassword());

        return jwt;
    }
}
