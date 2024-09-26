package com.boozallen.aissemble.upgrade.migration.v1_9_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArgocdValueFileSyncPolicyMigrationStep extends AbstractMigrationTest {
    private String configuration;

    @Given("an ArgoCD value file has {string} configuration defined")
    public void anArgoCDValueFileHasConfiguredSyncPolicyConfigurationDefined(String configuration) {
        this.configuration = configuration;
        testFile = getTestFile(String.format("v1_9_0/ArgocdValueFileSyncPolicyMigration/migration/%s-values.yaml", configuration));
    }

    @When("the 1.9.0 ArgoCD value file syncPolicy migration executes")
    public void theArgoCDTemplateSyncPolicyMigrationExecutes() {
        AbstractAissembleMigration migration = new ArgocdValueFileSyncPolicyMigration();
        performMigration(migration);
    }

    @Then("the value file is updated with expected syncPolicy value function")
    public void theSyncPolicyTemplateFunctionIsIncludedInTheTemplate() {
        File validatedFile = getTestFile(String.format("v1_9_0/ArgocdValueFileSyncPolicyMigration/validation/%s-values.yaml", configuration));
        assertLinesMatch("the value file is updated with expected syncPolicy value function.", validatedFile, testFile);
    }

    @Given("an ArgoCD value file has indent with 4 spaces")
    public void anArgoCDValueFileHasIndentWith4Spaces() {
        this.configuration = "indent-with-4-spaces";
        testFile = getTestFile("v1_9_0/ArgocdValueFileSyncPolicyMigration/migration/indent-with-4-spaces-values.yaml");
    }

    @Then("the file still has indent with 4 spaces")
    public void theFileStillHasIndentWith4Spaces() throws IOException {
        List<String> content = FileUtils.readAllFileLines(testFile);
        assertEquals("the file still has indent with 4 spaces", 4, YamlUtils.getIndentSpaces(content, 0));
    }

}
