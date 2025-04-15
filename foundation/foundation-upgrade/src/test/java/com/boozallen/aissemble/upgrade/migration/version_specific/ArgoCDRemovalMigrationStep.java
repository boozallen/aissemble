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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArgoCDRemovalMigrationStep extends AbstractMigrationTest {

    @Given("a project has yaml file under templates directory with following properties:")
    public void AProjectHasYamlFileUnderTemplatesDirectoriesWith(String properties) throws IOException {
        setTestFileToVersionMigration("ArgoCDRemovalMigration", "templates/argo-application.yaml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile.getAbsoluteFile()));
        writer.write(properties);
        writer.close();
    }

    @When("the ArgoCD removal migration is performed")
    public void theArgoCDRemovalMigrationIsPerformed() {
        ArgoCDRemovalMigration migration = new ArgoCDRemovalMigration();
        performMigration(migration);
    }

    @Then("the ArgoCD yaml is removed")
    public void theArgoCDYamlIsRemoved() {
        assertFalse("Failed to remove ArgoCD yaml file", testFile.exists());
    }

    @Then("the following files are removed in the parent directory:")
    public void theFollowingFilesAreRemovedInTheParentDirectory(List<String> files) {
        for (String file: files) {
            assertFalse(String.format("Failed to remove ArgoCD %s file", file), Files.exists(testFile.toPath().getParent().getParent().resolve(file)));
        }
        assertTrue("No ArgoCD application should not be removed", Files.exists(testFile.toPath().getParent().resolve("none-argo-application.yaml")));
    }

}
