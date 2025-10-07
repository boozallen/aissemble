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
import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import com.boozallen.aissemble.security.config.SecurityConfiguration;
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
