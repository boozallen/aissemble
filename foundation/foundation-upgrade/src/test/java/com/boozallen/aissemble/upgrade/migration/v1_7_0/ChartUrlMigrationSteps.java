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
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;


public class ChartUrlMigrationSteps extends AbstractMigrationTest {
    private static final String NEW_HELM_REPO_URL = "oci://ghcr.io/boozallen";
    private static final String OLD_HELM_REPO_URL = "https://old-helm-repository-url.com/";

    @After
    public void tearDown(){
        System.clearProperty("oldHelmRepositoryUrl");
    }

    @Given("a Chart file referencing the older Helm repository URL")
    public void a_chart_file_referencing_the_older_helm_repository_url() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/migration/kafka-cluster/Chart.yaml");
    }
    @Given("a Chart file")
    public void a_chart_file() {
        testFile = getTestFile("v1_7_0/HelmChartNameMigration/skip-migration/kafka-cluster/Chart.yaml");
    }

    @Given("the old Helm repository URL is provided through the system property")
    public void the_old_helm_repository_url_is_provided_through_the_system_property() {
        System.setProperty("oldHelmRepositoryUrl", OLD_HELM_REPO_URL);
    }

    @Given("the old Helm repository URL is not provided through the system property")
    public void the_old_helm_repository_url_is_not_provided_through_the_system_property() {
        System.clearProperty("oldHelmRepositoryUrl");
        assertNull(System.getProperty("oldHelmRepositoryUrl"));
    }

    @When("the Helm chart url migration executes")
    public void the_helm_chart_name_change_migration_executes() {
        performMigration(new HelmChartsRepositoryUrlMigration());
    }

    @Then("the Chart file is updated to use new Helm repository URL")
    public void the_chart_file_is_updated_to_use_new_helm_repository_url() {
        HashMap<String, Object> chartFileContents;
        try {
            chartFileContents = MigrationTestUtils.extractYamlContents(testFile);
            Object dependenciesObject = chartFileContents.get("dependencies");
            ArrayList<HashMap<String, Object>> dependencies = (ArrayList<HashMap<String, Object>>) dependenciesObject;

            for (HashMap<String, Object> dependency : dependencies) {
                String repositoryUrl = (String) dependency.get("repository");
                assertTrue(repositoryUrl.matches(NEW_HELM_REPO_URL));
            }
        } catch (IOException e) {
            fail(String.format("Unable to parse yaml contents due to exception: %s", e));
        }
    }

    @Then("the Chart file is not updated to use new Helm repository URL")
    public void the_chart_file_is_not_updated_to_use_new_helm_repository_url() {
        assertFalse("Old Helm chart repository URL not passed through system property", shouldExecute);
    }

}

