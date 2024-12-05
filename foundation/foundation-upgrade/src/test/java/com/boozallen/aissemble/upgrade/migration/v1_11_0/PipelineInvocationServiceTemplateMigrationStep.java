package com.boozallen.aissemble.upgrade.migration.v1_11_0;

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

public class PipelineInvocationServiceTemplateMigrationStep extends AbstractMigrationTest {
    private static final Logger logger = LoggerFactory.getLogger(PipelineInvocationServiceTemplateMigrationStep.class);
    public static final String MIGRATION_ROOT = "v1_11_0/PipelineInvocationServiceTemplateMigration/migration/%s/pipeline-invocation-service.yaml";
    private File validatedFile;

    @Given("a Pipeline Invocation Service template doesn't have helm.valueFiles configuration defined")
    public void PipelineInvocationServiceTemplateDoesNotHaveHelmValueFilesDefined() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "perform"));
    }

    @Given("a Pipeline Invocation Service template has helm.valueFiles configuration defined")
    public void PipelineInvocationServiceTemplateHasHelmValueFilesDefined() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "skip"));
    }

    @Given("a Pipeline Invocation Service template has indent with 4 spaces")
    public void PipelineInvocationServiceTemplateIndentedWith4Spaces() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "indent"));
    }

    @When("the 1.11.0 Pipeline Invocation Service ArgoCD template migration executes")
    public void PipelineInvocationServiceMigrationExecutes() {
        PipelineInvocationServiceTemplateMigration migration = new PipelineInvocationServiceTemplateMigration();
        performMigration(migration);
    }

    @Then("the helm.valueFiles template function is included in the template")
    public void helmValueFilesTemplateFunctionIsIncludeIntheTempate() {
        String file = testFile.getParent().replaceAll("^.+/migration", "") + "/" + testFile.getName();
        validatedFile = getTestFile("/v1_11_0/PipelineInvocationServiceTemplateMigration/validation/" + file);
        assertLinesMatch("The helm.valueFiles helm function is included in the template.", validatedFile, testFile);
    }

    @Then("the pipeline invocation service template migration is skipped")
    public void templateMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    @Then("the application template still has indent with 4 spaces")
    public void theApplicationTemplateStillHasIndentWith4Spaces() throws IOException {
        List<String> content = FileUtils.readAllFileLines(testFile);
        assertEquals("the file still has indent with 4 spaces", 4, YamlUtils.getIndentSpaces(content, 0));
    }
}
