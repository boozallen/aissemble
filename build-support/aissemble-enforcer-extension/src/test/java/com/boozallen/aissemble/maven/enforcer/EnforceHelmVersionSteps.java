package com.boozallen.aissemble.maven.enforcer;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Maven::Enforcer
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class EnforceHelmVersionSteps {

    private EnforceHelmVersion helmEnforcer;
    private EnforcerLogger enforcerLogger;
    private boolean flagged = false;

    @Before("@enforce-helm-version")
    public void setUp() {
        helmEnforcer = new EnforceHelmVersion();
        enforcerLogger = mock(EnforcerLogger.class);
        helmEnforcer.setLog(enforcerLogger);
    }

    @After("@enforce-helm-version")
    public void cleanup() {
        helmEnforcer = null;
        flagged = false;
    }
    
    @Given("a helm version {string}")
    public void a_helm_version(String currentVersion) {
        helmEnforcer.setCurrentHelmVersion(new DefaultArtifactVersion(currentVersion));
    }

    @When("the required helm version is {string}")
    public void the_required_helm_version_is(String requiredVersion) {
        helmEnforcer.setRequiredHelmVersion(requiredVersion);
    }

    @Then("the version is NOT flagged")
    public void the_version_is_not_flagged() {
        executeHelmEnforcerRule();
        assertFalse("The required helm version is higher than or equal to the current version ", flagged);
    }

    @Then("the version is flagged")
    public void the_version_is_flagged() {
        executeHelmEnforcerRule();
        assertTrue("The required helm version is lower than the current version ", flagged);
    }

    private void executeHelmEnforcerRule() {
        try {
            helmEnforcer.execute();
            flagged = false;
        } catch (EnforcerRuleException e) {
            flagged = true;
        }
    }
}
