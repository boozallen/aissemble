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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SparkWorkerDockerImageTagMigrationStep extends AbstractMigrationTest {
    public static final String MIGRATION_ROOT = "v1_11_0/SparkWorkerDockerImageTagMigration/migration/%s/%s-image-tag-values.yaml";

    @Given("a pipeline spark application yaml has image configuration with {string} tag")
    public void aPipelineSparkApplicationYamlWithTheLatestTagImageConfig(String tag) {
        if (StringUtils.isBlank(tag)) {
            tag = "no";
        }
        testFile = getTestFile(String.format(MIGRATION_ROOT, "perform", tag));
    }

    @Given("a pipeline spark application yaml has image configuration with version tag")
    public void aPipelineSparkApplicationYamlWithTheVersionTagImageConfig() {
        testFile = getTestFile(String.format(MIGRATION_ROOT, "skip", "version"));
    }

    @When("the 1.11.0 pipeline spark application image tag migration executes")
    public void sparkApplictionImageTagMigrationExecutes() {
        SparkWorkerDockerImageTagMigrationTest migration = new SparkWorkerDockerImageTagMigrationTest();
        performMigration(migration);
    }

    @Then("the image is tagged with project version")
    public void theImageIsTaggedWithProjectVersion() {
        String file = testFile.getParent().replaceAll("^.+/migration", "") + "/" + testFile.getName();
        File validatedFile = getTestFile("/v1_11_0/SparkWorkerDockerImageTagMigration/validation/" + file);
        assertLinesMatch("The spark worker docker image tag is updated with project version.", validatedFile, testFile);
    }

    @Then("the pipeline spark application image tag migration is skipped")
    public void theMigrationIsSkipped() {
        assertMigrationSkipped();
    }

    public class SparkWorkerDockerImageTagMigrationTest extends SparkWorkerDockerImageTagMigration {
        protected MavenProject getRootProject() {
            MavenProject project = new MavenProject();
            project.setVersion("1.2.3-SNAPSHOT");
            return project;
        }
    }
}
