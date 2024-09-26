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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArgocdTemplateSyncPolicyMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ArgocdTemplateSyncPolicyMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_9_0/ArgocdTemplateSyncPolicyMigration/migration";
    private File validatedFile;

    @Given("an ArgoCD template doesn't have syncPolicy configuration defined")
    public void anArgoCDTemplateDoesNotHaveSyncPolicyConfigurationDefined() {
        testFile = getTestFile(MIGRATION_ROOT + "/applicable-template.yaml");
    }

    @Given("a template that is not an ArgoCD application")
    public void aTemplateThatIsNotArgoCDApplication() {
        testFile = getTestFile(MIGRATION_ROOT + "/not-application-template.yaml");
    }

    @Given("an ArgoCD template file has indent with 4 spaces")
    public void anArgoCDTemplateFileHasIndentWith4Spaces() {
        testFile = getTestFile(MIGRATION_ROOT + "/indent-with-4-spaces-template.yaml");
    }

    @Given("an ArgoCD template that has hard-coded sync options")
    public void anArgoCDTemplateThatHasTheUpgradeWorkaround() {
        testFile = getTestFile(MIGRATION_ROOT + "/workaround.yaml");
    }

    @Given("an ArgoCD template with a syncPolicy that does not conform to the 1.8 upgrade workaround")
    public void anArgoCDTemplateWithASyncPolicyThatDoesNotConformToTheUpgradeWorkaround() {
        testFile = getTestFile(MIGRATION_ROOT + "/non-workaround-policy.yaml");
    }

    @When("the 1.9.0 ArgoCD template syncPolicy migration executes")
    public void theArgoCDTemplateSyncPolicyMigrationExecutes() {
        ArgocdTemplateSyncPolicyMigration migration = new ArgocdTemplateSyncPolicyMigration();
        performMigration(migration);
    }

    @When("the 1.9.0 ArgoCD template syncPolicy migration executes a second time")
    public void theArgoCDTemplateSyncPolicyMigrationExecutesASecondTime() {
        ArgocdTemplateSyncPolicyMigration migration = new ArgocdTemplateSyncPolicyMigration();
        performMigration(migration);
        theArgoCDTemplateSyncPolicyMigrationExecutes();
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

    @Then("a helm function to append syncOptions from Helm values is added to syncPolicy")
    public void aHelmFunctionToAppendSyncOptionsFromHelmValuesIsAddedToSyncPolicy() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("ArgoCD template with hard-coded values not updated correctly");
    }

    @Then("the syncPolicy is updated to set the automated configuration from the values file")
    public void theSyncPolicyIsUpdatedToSetTheAutomatedConfigurationFromTheValuesFile() throws IOException {
        Assert.assertTrue("`automated` was not added to workaround logic", Files.readString(testFile.toPath()).contains("automated:"));
    }

    @Then("the migration is reported as unsuccessful")
    public void theMigrationIsReportedAsUnsuccessful() {
        Assert.assertFalse("Migration did not fail for a non-standard syncPolicy", successful);
    }

    @Then("instructions for updating the file manually are added to the end of the template as a comment")
    public void instructionsForUpdatingTheFileManuallyAreAddedToTheEndOfTheTemplateAsAComment() {
        assertTestFileMatchesExpectedFile("Manual update instructions not added to unsupported file");
    }

    @Then("the syncPolicy migration is skipped")
    public void theSyncPolicyMigrationIsSkipped() {
        assertMigrationSkipped();
        assertTestFileMatchesExpectedFile("Template was not skipped on second execution of migration!");
    }
}
