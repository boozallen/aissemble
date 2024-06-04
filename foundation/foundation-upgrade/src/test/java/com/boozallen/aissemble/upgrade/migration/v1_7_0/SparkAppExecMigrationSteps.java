package com.boozallen.aissemble.upgrade.migration.v1_7_0;

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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class SparkAppExecMigrationSteps extends AbstractMigrationTest  {
    @Given("a pipeline pom file with one or more helm template commands using the aissemble-spark-application chart")
    public void aPipelinePomFileWithOneOrMoreHelmTemplateCommandsUsingTheAissembleSparkApplicationChart() {
        testFile = getTestFile("v1_7_0/SparkAppExecMigration/migration/pom.xml");
    }

    @When("the 1.7.0 spark app exec migration executes")
    public void theSparkAppExecMigrationExecutes() {
        performMigration(new SparkAppExecMigration());
    }

    @Then("the pom is updated to use the aissemble-spark-application-chart from the fully qualified URL")
    public void thePomIsUpdatedToUseTheAissembleSparkApplicationChartFromTheFullyQualifiedURL() throws IOException {
        assertTrue("File migration was incorrectly skipped", shouldExecute);
        assertTrue("File was not migrated successfully", successful);
        File migratedFile = getTestFile("v1_7_0/SparkAppExecMigration/migration/pom.xml");
        File validationFile = getTestFile("v1_7_0/SparkAppExecMigration/validation/pom.xml");

        assertTrue("Migrated file does not match expected output",
                FileUtils.contentEqualsIgnoreEOL(migratedFile, validationFile, null));
    }
}
