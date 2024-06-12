package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

 import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.boozallen.aissemble.upgrade.migration.extensions.PoetryMigrationTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static com.boozallen.aissemble.upgrade.migration.utils.PoetryMigrationUtils.getOldPackages;
import static com.boozallen.aissemble.upgrade.migration.utils.PoetryMigrationUtils.createPyproject;
import static com.boozallen.aissemble.upgrade.migration.utils.PoetryMigrationUtils.getPartialOldPythonPackages;
import static com.boozallen.aissemble.upgrade.migration.utils.PoetryMigrationUtils.getNewPackages;
import static com.boozallen.aissemble.upgrade.migration.utils.PoetryMigrationUtils.verifyPoetryMigration;


public class PoetryMigrationSteps extends AbstractMigrationTest {

    private static final String PYPROJECT_FILE = Paths.get("pyproject", "pyproject.toml").toString();
    private static AbstractPoetryMigration migration;

    @BeforeClass
    public void createTestPyproject() throws IOException {
        addTestFile(PYPROJECT_FILE);
    }

    @AfterClass
    public void deletePyproject() {
        getTestFile(PYPROJECT_FILE).delete();
    }

    @Before
    public void setTestFile() {
        testFile = getTestFile(PYPROJECT_FILE);
        migration = new PoetryMigrationTest();
    }

    @Given("a pyproject.toml file with old aiSSEMBLE Python dependency naming conventions and versions")
    public void a_pyproject_toml_file_with_all_old_ai_ssemble_python_dependencies() throws IOException {
        createPyproject(getOldPackages(migration.getPythonPackageMigrations()), testFile);
    }

    @Given("a pyproject.toml file with old and new aiSSEMBLE Python dependency naming conventions and versions")
    public void a_pyproject_toml_file_with_old_ai_ssemble_python_dependencies() throws IOException {
        createPyproject(getPartialOldPythonPackages(migration.getPythonPackageMigrations()), testFile);
    }

    @Given("a pyproject.toml file with new aiSSEMBLE Python dependencies")
    public void a_pyproject_toml_file_with_new_ai_ssemble_python_dependencies() {
        createPyproject(getNewPackages(migration.getPythonPackageMigrations()), testFile);
    }

    @When("the pyproject.toml file migration is executed")
    public void the_pyproject_toml_file_migration_is_executed() {
        performMigration(migration);
    }

    @Then("the migration is skipped")
    public void the_migration_is_skipped() {
        assertFalse(shouldExecute);
    }

    @Then("the dependencies are updated to the newest naming convention and version")
    public void the_dependencies_are_updated_to_the_newest_naming_convention() {
        verifyPoetryMigration(migration.getPythonPackageMigrations(), testFile);
    }

}
