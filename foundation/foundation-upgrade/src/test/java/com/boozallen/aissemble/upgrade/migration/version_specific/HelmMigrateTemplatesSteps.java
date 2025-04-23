package com.boozallen.aissemble.upgrade.migration.version_specific;

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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class HelmMigrateTemplatesSteps extends AbstractMigrationTest {

    private final String BASE_DIRECTORY = getTestFile("migration-deploy/src/main/resources/").toString();
    private final String SOURCE_TEMPLATES_DIRECTORY = "./target/test-classes/test-files/TemplatesToAppsMigration/migration/";
    private final String SOURCE_ROOT_CHARTS_DIRECTORY = "./target/test-classes/test-files/TemplatesToAppsMigration/migration-deploy/";
    private final String PRE_MIGRATION_TEMPLATES_DIRECTORY = BASE_DIRECTORY + "/templates/";
    private final String PRE_MIGRATION_CHARTS_DIRECTORY = BASE_DIRECTORY;
    private final String MIGRATED_ROOT_CHARTS_DIRECTORY = BASE_DIRECTORY + "/apps/common-infrastructure/";
    private final String MIGRATED_APPS_COMMON_INFRASTRUCTURE_TEMPLATES_DIRECTORY = BASE_DIRECTORY + "/apps/common-infrastructure/templates/";

    private final String REMOTE_AUTH_SECRET_FILE_NAME = "remote-auth-secret.yaml";
    private final String YAML_WITH_ARGOCD_FILE_NAME = "s3-local.yaml";
    private final String CHART_YAML_FILE = "Chart.yaml";
    private final String VALUES_YAML_FILE = "values.yaml";

    @Given("a project has files in the templates folder")
    public void a_project_has_files_in_the_templates_folder() {
        createTestFolders();
        copyFileToFolder(Paths.get(SOURCE_TEMPLATES_DIRECTORY, REMOTE_AUTH_SECRET_FILE_NAME), Paths.get(PRE_MIGRATION_TEMPLATES_DIRECTORY, REMOTE_AUTH_SECRET_FILE_NAME));
        copyFileToFolder(Paths.get(SOURCE_TEMPLATES_DIRECTORY, YAML_WITH_ARGOCD_FILE_NAME), Paths.get(PRE_MIGRATION_TEMPLATES_DIRECTORY, YAML_WITH_ARGOCD_FILE_NAME));
    }

    @When("the helm templates migration is performed")
    public void the_helmfile_templates_migration_is_performed() {
        HelmTemplatesMigrationTest helmTemplatesMigrationTest = new HelmTemplatesMigrationTest();



        File templateFileThatCanBeMoved = Paths.get(PRE_MIGRATION_TEMPLATES_DIRECTORY, REMOTE_AUTH_SECRET_FILE_NAME).toFile();
        boolean canMigrate = helmTemplatesMigrationTest.shouldExecuteOnFileImpl(templateFileThatCanBeMoved);
        if(canMigrate) {
            helmTemplatesMigrationTest.performMigration(templateFileThatCanBeMoved);
        }

        File templateFileThatCanNotBeMoved = Paths.get(PRE_MIGRATION_TEMPLATES_DIRECTORY, YAML_WITH_ARGOCD_FILE_NAME).toFile();
        canMigrate = helmTemplatesMigrationTest.shouldExecuteOnFileImpl(templateFileThatCanNotBeMoved);
        if(canMigrate) {
            // This should not be called.  Will be verified in the next step
            helmTemplatesMigrationTest.performMigration(templateFileThatCanNotBeMoved);
        }
    }

    @Then("the yaml files are moved to apps\\/common-infrastructure\\/templates")
    public void the_yaml_files_are_moved_to_apps_common_infrastructure_templates() {
        File templateFileThatCanBeMoved = Paths.get(MIGRATED_APPS_COMMON_INFRASTRUCTURE_TEMPLATES_DIRECTORY, REMOTE_AUTH_SECRET_FILE_NAME).toFile();
        File templateFileThatCanNotBeMoved = Paths.get(MIGRATED_APPS_COMMON_INFRASTRUCTURE_TEMPLATES_DIRECTORY, YAML_WITH_ARGOCD_FILE_NAME).toFile();

        assertTrue("Migrate-able file was not migrated", templateFileThatCanBeMoved.exists());
        assertFalse("Non-migrate-able file was migrated", templateFileThatCanNotBeMoved.exists());
    }

    @Given("a project has files in the root deploy folder")
    public void a_project_has_files_in_the_root_deploy_folder() {
        copyFileToFolder(Paths.get(SOURCE_ROOT_CHARTS_DIRECTORY, CHART_YAML_FILE), Paths.get(PRE_MIGRATION_CHARTS_DIRECTORY, CHART_YAML_FILE));
        copyFileToFolder(Paths.get(SOURCE_ROOT_CHARTS_DIRECTORY, VALUES_YAML_FILE), Paths.get(PRE_MIGRATION_CHARTS_DIRECTORY, VALUES_YAML_FILE));
    }

    @When("the root deploy files migration is performed")
    public void the_root_deploy_files_migration_is_performed() {
        HelmRootChartMigrationTest helmRootChartMigrationTest = new HelmRootChartMigrationTest();

        File rootHelmChartFileThatCanBeMoved = Paths.get(PRE_MIGRATION_CHARTS_DIRECTORY, CHART_YAML_FILE).toFile();
        boolean canMigrate = helmRootChartMigrationTest.shouldExecuteOnFileImpl(rootHelmChartFileThatCanBeMoved);
        if(canMigrate) {
            System.out.println("ShouldExecute was true");
            helmRootChartMigrationTest.performMigration(rootHelmChartFileThatCanBeMoved);
        }

        rootHelmChartFileThatCanBeMoved = Paths.get(PRE_MIGRATION_CHARTS_DIRECTORY, VALUES_YAML_FILE).toFile();
        canMigrate = helmRootChartMigrationTest.shouldExecuteOnFileImpl(rootHelmChartFileThatCanBeMoved);
        if(canMigrate) {
            System.out.println("ShouldExecute was true");
            helmRootChartMigrationTest.performMigration(rootHelmChartFileThatCanBeMoved);
        }
    }

    @Then("the yaml files are moved to apps\\/common-infrastructure")
    public void the_helm_files_are_moved_to_apps_common_infrastructure() {
        File rootChartFile = Paths.get(MIGRATED_ROOT_CHARTS_DIRECTORY, CHART_YAML_FILE).toFile();
        File rootValuesFile = Paths.get(MIGRATED_ROOT_CHARTS_DIRECTORY, VALUES_YAML_FILE).toFile();

        assertTrue("Root Chart.yaml file was not migrated", rootChartFile.exists());
        assertTrue("Root values.yaml file was not migrated", rootValuesFile.exists());
    }

    public void createTestFolders() {
        File dir = new File(PRE_MIGRATION_TEMPLATES_DIRECTORY);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Templates folder can't be created: " + dir);
        }

        dir = new File(MIGRATED_APPS_COMMON_INFRASTRUCTURE_TEMPLATES_DIRECTORY);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("apps/common-infrastructure folder can't be created: " + dir);
        }

        dir = new File(MIGRATED_ROOT_CHARTS_DIRECTORY);
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("migration-deploy/src/main/resources folder can't be created: " + dir);
        }
    }

    public void copyFileToFolder(Path sourcePath, Path destinationPath) {
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy file: " + e.getMessage());
        }
    }
}
