package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
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

import static org.junit.Assert.assertEquals;

import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecisionPoint;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AuthorizationSteps {
    private String resource;
    private String action;
    private String subject;
    private PolicyDecision decision;

    private PolicyDecisionPoint pdp = PolicyDecisionPoint.getInstance();

    @Given("a resource {string} and subject {string}")
    public void a_resource_and_subject(String resource, String subject) {
        this.resource = resource;
        this.action = "ballInPlay";
        this.subject = subject;
    }

    @Given("a resource action {string} and subject {string}")
    public void a_resource_action_and_subject(String action, String subject) {
        this.resource = "twoStrikeAtBat";
        this.action = action;
        this.subject = subject;
    }

    @When("a policy decision is requested")
    public void a_policy_decision_is_requested() {
        decision = pdp.isAuthorized(subject, resource, action);
    }

    @Then("a {string} decision is returned")
    public void a_decision_is_returned(String expectedDecision) {
        assertEquals(PolicyDecision.valueOf(expectedDecision), this.decision);
    }
}
