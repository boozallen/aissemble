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
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArgocdTemplateSyncPolicyMigrationStep extends AbstractMigrationTest {
    private File validatedFile;

    @Given("an ArgoCD template doesn't have syncPolicy configuration defined")
    public void anArgoCDTemplateDoesNotHaveSyncPolicyConfigurationDefined() {
        testFile = getTestFile("v1_9_0/ArgocdTemplateSyncPolicyMigration/migration/applicable-template.yaml");
    }

    @Given("an ArgoCD template has the syncPolicy configuration defined")
    public void anArgoCDTemplateHasSyncPolicyConfigurationDefined() {
        testFile = getTestFile("v1_9_0/ArgocdTemplateSyncPolicyMigration/migration/inapplicable-template.yaml");
    }

    @Given("a template that is not an ArgoCD application")
    public void aTemplateThatIsNotArgoCDApplication() {
        testFile = getTestFile("v1_9_0/ArgocdTemplateSyncPolicyMigration/migration/not-application-template.yaml");
    }

    @Given("an ArgoCD template file has indent with 4 spaces")
    public void anArgoCDTemplateFileHasIndentWith4Spaces() {
        testFile = getTestFile("v1_9_0/ArgocdTemplateSyncPolicyMigration/migration/indent-with-4-spaces-template.yaml");
    }

    @When("the 1.9.0 ArgoCD template syncPolicy migration executes")
    public void theArgoCDTemplateSyncPolicyMigrationExecutes() {
        AbstractAissembleMigration migration = new ArgocdTemplateSyncPolicyMigration();
        performMigration(migration);
    }

    @Then("the syncPolicy template function is included in the template")
    public void theSyncPolicyTemplateFunctionIsIncludedInTheTemplate() {
        validatedFile = getTestFile("/v1_9_0/ArgocdTemplateSyncPolicyMigration/validation/" + testFile.getName());
        assertLinesMatch("The syncPolicy helm function is included in the template.", validatedFile, testFile);
    }

    @Then("the template is unchanged")
    public void theTemplateIsUnchanged() {
        validatedFile = getTestFile("/v1_9_0/ArgocdTemplateSyncPolicyMigration/validation/" + testFile.getName());
        assertLinesMatch("The ArgoCD template is not impacted by migration!", validatedFile, testFile);
    }

    @Then("the application template still has indent with 4 spaces")
    public void theApplicationTemplateStillHasIndentWith4Spaces() throws IOException {
        List<String> content = FileUtils.readAllFileLines(testFile);
        assertEquals("the file still has indent with 4 spaces", 4, YamlUtils.getIndentSpaces(content, 0));
    }

}
