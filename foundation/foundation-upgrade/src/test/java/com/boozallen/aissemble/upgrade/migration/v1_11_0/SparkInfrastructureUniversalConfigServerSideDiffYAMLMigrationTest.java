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

import java.io.FileNotFoundException;
import java.util.Scanner;


public class SparkInfrastructureUniversalConfigServerSideDiffYAMLMigrationTest extends AbstractMigrationTest {
    @Given("spark infrastructure yaml file")
    public void aSparkInfrastructureYamlFile()
    {
        testFile = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/default-values.yaml");

    }

    @When("the spark infrastructure configuration serverside diff migration executes")
    public void theSparkInfrastructureConfigurationServerSideDiffMigrationExecutes()
    {
        SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration migration = new SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration();
        performMigration(migration);
    }

    @Then("spark-infrastructure.yaml is updated to add server side diff annotation.")
    public void theServerSideDiffAnnotationAdded() throws FileNotFoundException {
        assertMigrationSuccess();
        var updatedValue = getTestFile("v1_11_0/SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration/migration/updated-values.yaml");
        Scanner sc = new Scanner(updatedValue);

        sc.useDelimiter("\\Z");

        System.out.println(sc.next());

        Scanner sc1 = new Scanner(testFile);

        sc1.useDelimiter("\\Z");

        System.out.println(sc1.next());
        assertLinesMatch("Yaml file not updated to add server side diff annotation: " + testFile.getName(), testFile, updatedValue);
    }
}
