package com.boozallen.aissemble.pipeline.invocation.service;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
