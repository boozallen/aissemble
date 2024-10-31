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
 

public class PomDependencyVersionMigrationSteps extends AbstractMigrationTest {
    @Given("a POM that references dependencies previously managed by the aiSSEMBLE BOM")
    public void a_pom_that_references_dependencies_previously_managed_by_the_aissemble_bom() {
        testFile = getTestFile("v1_10_0/PomDependencyVersionMigration/migration/all-dependencies-pom.xml");
    }

    @Given("the POM has the dependencies in profiles and dependency management")
    public void the_pom_has_the_dependencies_in_profiles_and_dependency_management() {
        testFile = getTestFile("v1_10_0/PomDependencyVersionMigration/migration/dependency-management-profile-pom.xml");
    }

    @Given("the dependencies are all using the updated version")
    public void the_dependencies_are_all_using_the_updated_version() {
        testFile = getTestFile("v1_10_0/PomDependencyVersionMigration/validation/all-dependencies-pom.xml");
    }

    @Given("a POM that does not contain any dependencies previously managed by the aiSSEMBLE BOM")
    public void a_pom_that_does_not_contain_any_dependencies_previously_managed_by_the_aissemble_bom() {
        testFile = getTestFile("v1_10_0/PomDependencyVersionMigration/migration/skip-pom.xml");
    }

    @When("the POM Dependency Version migration executes")
    public void the_pom_dependency_version_migration_executes() {
        performMigration(new PomDependencyVersionMigration());
    }

    @Then("the dependencies are updated to use the necessary version")
    public void the_dependencies_are_updated_to_use_the_necessary_version() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Dependency versions were not updated correctly following the migration");
    }

    @Then("the POM Dependency Version migration is skipped")
    public void the_pom_dependency_version_migration_is_skipped() {
        assertMigrationSkipped();
    }
}
