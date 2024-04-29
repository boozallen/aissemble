package com.boozallen.aiops.security.client;

/*-
 * #%L
 * AIOps Foundation::AIOps Core Security::AIOps Policy Decision Point Client
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.security.authorization.models.AuthRequest;
import com.boozallen.aiops.security.authorization.models.PDPRequest;
import com.boozallen.aiops.security.authorization.policy.PolicyDecision;
import com.boozallen.aiops.security.config.SecurityConfiguration;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

public class PolicyDecisionPointClient {
    private final SecurityConfiguration config = KrauseningConfigFactory.create(SecurityConfiguration.class);

    public PolicyDecisionPointClient() {
    }

    public String getPolicyDecision(PDPRequest request) {
        String decision = "";
        //If authentication is disabled we should permit
        if(config.authenticationEnabled()){
            ResteasyClient client = new ResteasyClientBuilderImpl().build();
            ResteasyWebTarget target = client.target(config.getPdpHost());
            PolicyDecisionPointProxy simpleClient = target.proxy(PolicyDecisionPointProxy.class);
            decision = simpleClient.getDecision(request);
        } else {
            decision = PolicyDecision.PERMIT.toString();
        }

        return decision;
    }

    public String authenticate(AuthRequest authRequest) {
        String jwt = "";
        if(config.authenticationEnabled()) {
            ResteasyClient client = new ResteasyClientBuilderImpl().build();
            ResteasyWebTarget target = client.target(config.getPdpHost());
            PolicyDecisionPointProxy simpleClient = target.proxy(PolicyDecisionPointProxy.class);
            jwt = simpleClient.authenticate(authRequest);
        } else {
            jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI3ZDZmMWZlNS05YzZiLTQ1ZGEtODlmMS0yMDM5YWIwNWZhNDEiLCJzdWIiOiJhaW9wcyIsImF1ZCI6ImF1ZGllbmNlIiwibmJmIjoxNjI0OTc2MDI5LCJpYXQiOjE2MjQ5NzYwMjksImV4cCI6MTkyNDk4MDc5NywiaXNzIjoiYWlvcHMuYXV0aG9yaXR5In0.eUBC2ink77XRf5n5JIXlLZR-fBiRmGrqo1TBFz46yZhWDY38dsh30flELE8gO5SG2rUSIe-VmmjSny8PFLNGwy5MGLwr9z56HoH7OrejJeEzCa1yBl67VWgUZhoDy3RzvARfdBnUstfigHYeQA2ECvW-b2kppYJPVUNX2uKmwfZupwqCGqIX56s7qntV0dUAjpC_KiZ3fjUz1HXqK_evWos0xPVT8XOB2ZADhh87kf7LmocYQ4Y-Z_fsou6jqYh1lQT8WeI2AKskE613nSqmTA2bax5-dOFXKWKLy8t5glyjkdqFZVrLrNkK9tXqNYpZ8efIkZKOu7T9TlsvkHU1XQ";
        }

        return jwt;
    }
}
