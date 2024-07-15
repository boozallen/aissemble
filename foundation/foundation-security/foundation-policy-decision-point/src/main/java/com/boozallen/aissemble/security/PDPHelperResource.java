package com.boozallen.aissemble.security;

/*-
 * #%L
 * AIOps Foundation::AIOps Core Security::AIOps Policy Decision Point
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.security.authorization.models.AuthRequest;
import com.boozallen.aissemble.security.authorization.models.PDPRequest;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import com.boozallen.aissemble.security.authorization.AissembleSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.AissembleSimpleSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecisionPoint;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class PDPHelperResource {
    private AissembleSecureTokenServiceClient tokenClient = new AissembleSimpleSecureTokenServiceClient();
    private PolicyDecisionPoint pdp = PolicyDecisionPoint.getInstance();

    @POST
    @Path("/pdp")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String gatPdpDecision(PDPRequest pdpRequest) {
        Jws<Claims> jws = tokenClient.parseToken(pdpRequest.getJwt());
        Claims claims = jws.getBody();

        PolicyDecision policyDecision = pdp.isAuthorized(claims.getSubject(), pdpRequest.getResource(), pdpRequest.getAction());

        return policyDecision.toString();
    }

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String authenticate(AuthRequest authRequest) {
        AissembleSecureTokenServiceClient aiopsSecureTokenServiceClient = new AissembleSimpleSecureTokenServiceClient();
        String jwt = aiopsSecureTokenServiceClient.authenticate(authRequest.getUsername(), authRequest.getPassword());

        return jwt;
    }
}
