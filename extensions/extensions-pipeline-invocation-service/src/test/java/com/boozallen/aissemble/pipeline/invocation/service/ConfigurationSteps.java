package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
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

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfigurationSteps {
    @Inject
    PipelineInvocationAgent agent;

    private PipelineInvocationAgent.FailureStrategy expectedFailureStrategy;
    private String referenceAppName;
    @When("an application is configured to override the default failure behavior")
    public void an_application_is_configured_to_override_the_default_failure_behavior() {
        // No-Op, configured via test/resources/application.properties
        expectedFailureStrategy = PipelineInvocationAgent.FailureStrategy.EXCEPTIONAL;
        referenceAppName = "sampleApplication";
    }

    @When("an application is not configured to override the default failure behavior")
    public void an_application_is_not_configured_to_override_the_default_failure_behavior() {
        // No-Op, configured via test/resources/application.properties
        expectedFailureStrategy = PipelineInvocationAgent.FailureStrategy.LOG;
        referenceAppName = "other";
    }

    @Then("the correct behavior pattern will be identified for use")
    public void the_correct_behavior_pattern_will_be_identified_for_use() {
        assert(agent.getPipelineFailureStrategy(referenceAppName) == expectedFailureStrategy);
    }
}
