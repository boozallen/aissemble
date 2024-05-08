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
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChartNameMigrationSteps extends AbstractMigrationTest {

    @Given("a value file using the older Helm chart naming convention")
    public void a_value_file_using_the_older_helm_chart_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/migration/kafka-cluster/values.yaml");
    }

    @When("the Helm chart name change migration executes")
    public void the_helm_chart_name_change_migration_executes() {
        performMigration(new HelmChartsNameMigration());
    }

    @Then("the value file is migrated to use new naming convention")
    public void the_value_file_is_migrated_to_use_new_naming_convention() {
        HashMap<String, Object> valueFileContents;
        try {
            valueFileContents = MigrationTestUtils.extractYamlContents(testFile);
            String firstKey = valueFileContents.keySet().iterator().next();

            assertTrue(firstKey.startsWith("aissemble-") && firstKey.endsWith("-chart"));
        } catch (IOException e) {
            fail(String.format("Unable to parse yaml contents due to exception: %s", e));
        }
    }

    @Given("a Chart file using the older Helm chart naming convention")
    public void a_chart_file_using_the_older_helm_chart_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/migration/kafka-cluster/Chart.yaml");
    }

    @Then("the Chart file is migrated to use new naming convention")
    public void the_chart_file_is_migrated_to_use_new_naming_convention() {
        HashMap<String, Object> chartFileContents;
        try {
            chartFileContents = MigrationTestUtils.extractYamlContents(testFile);
            Object dependenciesObject = chartFileContents.get("dependencies");
            ArrayList<HashMap<String, Object>> dependencies = (ArrayList<HashMap<String, Object>>) dependenciesObject;

            for (HashMap<String, Object> dependency : dependencies) {
                String dependencyName = (String) dependency.get("name");

                assertTrue(dependencyName.startsWith("aissemble-") && dependencyName.endsWith("-chart"));
            }

        } catch (IOException e) {
            fail(String.format("Unable to parse yaml contents due to exception: %s", e));
        }
    }

    @Given("a value file using the new Helm chart naming convention")
    public void a_value_file_using_the_new_helm_chart_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/skip-migration/kafka-cluster/values.yaml");
    }

    @Then("the value file migration is skipped")
    public void the_value_file_migration_is_skipped() {
        assertFalse("Value file is already using new Helm chart naming convention", shouldExecute);
        
    }

    @Given("a Chart file using the new Helm chart naming convention")
    public void a_chart_file_using_the_new_helm_chart_naming_convention() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/skip-migration/kafka-cluster/Chart.yaml");
        
    }

    @Then("the Chart file migration is skipped")
    public void the_chart_file_migration_is_skipped() {
        assertFalse("Chart file is already using new Helm chart naming convention", shouldExecute);
    }

}

