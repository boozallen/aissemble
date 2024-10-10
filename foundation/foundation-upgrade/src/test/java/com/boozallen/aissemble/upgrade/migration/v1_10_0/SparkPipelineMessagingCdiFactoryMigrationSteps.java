package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.util.Arrays;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SparkPipelineMessagingCdiFactoryMigrationSteps extends AbstractMigrationTest {
    private static String TEST_DIR = "v1_10_0/SparkPipelineMessagingMigration/migration/";
    private String dependency;
    private SparkPipelineMessagingCdiFactoryMigration migration = new SparkPipelineMessagingCdiFactoryMigration();;
    private MavenProject mavenProject = new MavenProject();

    @Given("a maven project with the {string} packaging type")
    public void a_maven_project_with_the_packaging_type(String packaging) {
        this.mavenProject.setPackaging(packaging);
    }

    @Given("a maven project {string} dependency")
    public void a_maven_project_dependency(String dependency) {
        this.dependency = dependency;
        if (dependency.equals("smallrye-reactive-messaging-kafka")) {
            Dependency kafkaDependency = new Dependency();
            kafkaDependency.setGroupId("io.smallrye.reactive");
            kafkaDependency.setArtifactId(dependency);

            this.mavenProject.setDependencies(Arrays.asList(kafkaDependency));
            this.testFile = getTestFileInDir("smallrye-kafka/src/main/java/test/path/cdi/CdiContainerFactory.java");
        } else {
            Dependency otherDependency = new Dependency();
            otherDependency.setGroupId("io.smallrye.reactive");
            otherDependency.setArtifactId(dependency);

            this.mavenProject.setDependencies(Arrays.asList(otherDependency));
            this.testFile = getTestFileInDir("smallrye-non-kafka/src/main/java/test/path/cdi/CdiContainerFactory.java");
        }
    }

    @Given("the pipeline module has already been migrated")
    public void the_pipeline_module_has_already_been_migrated() {
        if (dependency.equals("smallrye-reactive-messaging-kafka")) {
            this.testFile = getTestFile("v1_10_0/SparkPipelineMessagingMigration/validation/smallrye-kafka/src/main/java/test/path/cdi/CdiContainerFactory.java");
        } else {
            this.testFile = getTestFile("v1_10_0/SparkPipelineMessagingMigration/validation/smallrye-non-kafka/src/main/java/test/path/cdi/CdiContainerFactory.java");
        }
    }

    @Given("no maven project smallrye dependencies")
    public void no_maven_project_smallrye_dependencies() {
        Dependency otherDependency = new Dependency();
            otherDependency.setGroupId("io.test");
            otherDependency.setArtifactId("test.dependency");
            this.mavenProject.setDependencies(Arrays.asList(otherDependency));
    }

    @When("the Spark Pipeline Messaging migration executes")
    public void the_spark_pipeline_messaging_migration_executes() {
        this.migration.setMavenProject(this.mavenProject);
        performMigration(this.migration);
    }

    @Then("the CDI container factory is updated with the messaging and kafka context objects")
    public void the_CDI_container_factory_is_updated_with_the_messaging_and_kafka_context_objects() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("CDI container factory was not updated with the new kafka and messaging context objects");
    }

    @Then("the CDI container factory is updated with the messaging context object")
    public void the_CDI_container_factory_is_updated_with_the_messaging_context_object() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("CDI container factory was not updated with the new messaging context objects");
    }

    @Then("the Spark Pipeline Messaging migration is skipped")
    public void the_spark_pipeline_messaging_migration_is_skipped() {
        assertMigrationSkipped();
    }

    protected File getTestFileInDir(String Filename) {
        return getTestFile(TEST_DIR + Filename);
    }
}
