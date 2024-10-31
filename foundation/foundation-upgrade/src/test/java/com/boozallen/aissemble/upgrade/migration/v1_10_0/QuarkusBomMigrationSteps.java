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

public class QuarkusBomMigrationSteps extends AbstractMigrationTest {
    @Given("a POM that references the {string} Quarkus BOM")
    public void a_pom_that_references_the_quarkus_bom(String quarkusBomType) {
        testFile = getTestFile("v1_10_0/QuarkusBomMigration/migration/" + quarkusBomType + "-pom.xml");
    }

    @Given("a POM that does not contain a Quarkus BOM")
    public void a_pom_that_does_not_contain_a_quarkus_bom() {
        testFile = getTestFile("v1_10_0/QuarkusBomMigration/validation/quarkus-bom-pom.xml");
    }

    @When("the Quarkus Bom Migration executes")
    public void the_quarkus_bom_migration_executes() {
        performMigration(new QuarkusBomMigration());
    }

    @Then("the Quarkus BOM is updated to the aiSSEMBLE Quarkus BOM")
    public void the_quarkus_bom_is_updated_to_the_aissemble_quarkus_bom() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Quarkus BOM was not updated correctly to aiSSEMBLE Quarkus BOM");
    }

    @Then("the Quarkus Bom Migration is skipped")
    public void the_quarkus_bom_migration_is_skipped() {
        assertMigrationSkipped();
    }
}
