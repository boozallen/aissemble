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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChartModuleMigrationSteps extends AbstractMigrationTest {
    @Given("a value file using the older Helm chart module name")
    public void a_value_file_using_the_older_helm_chart_module_name() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/migration/kafka-cluster/values.yaml");

    }

    @When("the Helm module name change migration executes")
    public void the_helm_module_name_change_migration_executes() {
        performMigration(new HelmChartsModuleMigration());
    }


    @Then("the value file is migrated to use new module name")
    public void the_value_file_is_migrated_to_use_new_module_name(){
        assertTrue(doesNotContainOldModuleName(testFile));
    }

    @Given("a Chart file using the older Helm chart module name")
    public void a_chart_file_using_the_older_helm_chart_module_name() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/migration/kafka-cluster/Chart.yaml");
    }

    @Then("the Chart file is migrated to use new module name")
    public void the_chart_file_is_migrated_to_use_new_module_name() {
        assertTrue(doesNotContainOldModuleName(testFile));
    }

    @Given("a value file using the new Helm chart module naming convention")
    public void a_value_file_using_the_new_helm_chart_module_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/skip-migration/kafka-cluster/values.yaml");
    }

    @Given("a Chart file using the new Helm chart module naming convention")
    public void a_chart_file_using_the_new_helm_chart_module_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/skip-migration/kafka-cluster/values.yaml");

    }

    @Then("the value file module name migration is skipped")
    public void the_value_file_module_name_migration_is_skipped() {
        assertFalse("Value file is already using new Helm module name", shouldExecute);

    }

    @Then("the Chart file module name migration is skipped")
    public void the_chart_file_module_name_migration_is_skipped() {
        assertFalse("Chart file is already using new Helm module name", shouldExecute);
    }

    private static boolean doesNotContainOldModuleName(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(file)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("extensions-helm-kafka")) {
                    return false;
                }
            }
        } catch (IOException e){
            fail(String.format("unable to parse yaml file due to exception: %s", e));
        }
        return true;
    }
}

