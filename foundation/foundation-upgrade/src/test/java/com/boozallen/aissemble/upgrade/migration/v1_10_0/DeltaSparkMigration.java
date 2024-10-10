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

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeltaSparkMigration extends AbstractMigrationTest {
    @Given("a spark-application values file that references delta-core\\/delta-storage in sparkApp.spec.deps.jars")
    public void aSparkApplicationValuesFileThatReferencesDeltaCoreDeltaStorageInSparkAppSpecDepsJars() {
        testFile = getTestFile("v1_10_0/DeltaSparkYamlMigration/migration/spark-app-values.yaml");
    }

    @Given("a SparkApplication file that references delta-core\\/delta-storage in spec.deps.jars")
    public void aSparkApplicationFileThatReferencesDeltaCoreDeltaStorageInSpecDepsJars() {
        testFile = getTestFile("v1_10_0/DeltaSparkYamlMigration/migration/spark-app.yaml");
    }

    @Given("a POM that references the delta-core package")
    public void aPOMThatReferencesTheDeltaCorePackage() {
        testFile = getTestFile("v1_10_0/DeltaSparkPomMigration/migration/pom.xml");
    }

    @When("the 1.10.0 DeltaSpark yaml migration executes")
    public void theDeltaSparkYamlMigrationExecutes() {
        DeltaSparkYamlMigration migration = new DeltaSparkYamlMigration();
        performMigration(migration);
    }

    @When("the 1.10.0 DeltaSpark POM migration executes")
    public void theDeltaSparkPOMMigrationExecutes() {
        DeltaSparkPomMigration migration = new DeltaSparkPomMigration();
        performMigration(migration);
    }

    @Then("the delta-lake jar coordinates are updated to 3.2.1")
    public void theDeltaLakeJarCoordinatesAreUpdatedTo() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Yaml file not updated with new DeltaLake jars: " + testFile.getName());
    }

    @Then("delta-core is updated to delta-spark and the version is set to the version.delta property")
    public void deltaCoreIsUpdatedToDeltaSparkAndTheVersionIsSetToTheVersionDeltaProperty() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("POM not updated with new DeltaLake dependencies.");
    }
}
