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

import com.boozallen.aissemble.pipeline.invocation.service.serialization.PipelineInvocationRequest;
import com.boozallen.aissemble.pipeline.invocation.service.util.exec.HelmCommandExecutor;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@ApplicationScoped
public class HelmCommandSteps {
    @Inject
    HelmCommandExecutor executor;

    @Inject
    PipelineInvocationAgent agent;

    @ConfigProperty(name = "aissemble.helm.repo.url")
    private String aissembleHelmRepoUrl;
    
    @ConfigProperty(name = "aissemble.helm.repo.protocol")
    private String aissembleHelmRepoProtocol;

    private final String test_release_name = "java-pipeline";

    private List<String> referenceArgs;
    private PipelineInvocationRequest request;

    @When("helm install command base arguments are configured")
    public void helm_install_command_base_args_are_configured() {
        referenceArgs = executor.createBaseHelmInstallArgs(test_release_name);
    }

    @Then("the appropriate release name will be present")
    public void the_appropriate_release_name_will_be_present() {
        assertTrue(referenceArgs.get(1).equalsIgnoreCase(test_release_name));
    }

    @Then("the appropriate helm command will be present")
    public void the_appropriate_helm_command_will_be_present() {
        assertTrue(referenceArgs.get(0).equalsIgnoreCase("install"));
    }

    @Then("a valid OCI helm chart reference will be specified")
    public void a_valid_OCI_helm_chart_reference_will_be_specified() {
        assertTrue(referenceArgs.get(2).contains(aissembleHelmRepoProtocol + "://" + aissembleHelmRepoUrl + "/aissemble-spark-application-chart"));
    }

    @Then("a valid version will be specified")
    public void a_valid_version_will_be_specified() {
        assertFalse(referenceArgs.get(referenceArgs.indexOf("--version") + 1).isBlank());
    }

    @When("a helm command is built using the {} execution profile")
    public void a_helm_command_is_built_using_the_execution_profile(String profile) {
        PipelineInvocationRequest req = new PipelineInvocationRequest();
        req.setProfile(profile);
        req.setApplicationName(test_release_name);
        referenceArgs = agent.buildHelmValuesArgs(req);
    }

    @Then("the appropriate values arguments for {} will be specified")
    public void the_appropriate_values_arguments_for_profile_will_be_specified(String profile) {
        ExecutionProfile executionProfile = ExecutionProfile.valueOf(profile.toUpperCase());
        assertEquals(referenceArgs.size(), executionProfile.getLayers().size() * 2);

        for(int i = 0; i < executionProfile.getLayers().size(); i++) {
            assertEquals(referenceArgs.get(i*2), "--values");
            assertTrue(referenceArgs.get(i*2+1).contains(executionProfile.getLayers().get(i) + "-values.yaml"));
        }
    }

    @When("a helm command is built containing values overrides")
    public void a_helm_command_is_built_containing_values_overrides() {
        request = new PipelineInvocationRequest();
        request.setOverrideValues(Map.of(
                "myKey", "myVal",
                "nextKey", "nextVal"
        ));
        referenceArgs = agent.buildFinalValuesOverrides(request);
    }

    @Then("the values overrides are present in the resulting argument list")
    public void the_values_overrides_are_present_in_the_resulting_argument_list() {
        request.getOverrideValues().forEach((key, value) -> {
            String assignment = key + "=" + value;
            assertTrue(referenceArgs.contains(assignment));
            assertEquals(1, referenceArgs.indexOf(assignment) % 2);
        });

        for(int i = 0; i < referenceArgs.size(); i+=2) {
            assertEquals("--set", referenceArgs.get(i));
        }
    }
}
