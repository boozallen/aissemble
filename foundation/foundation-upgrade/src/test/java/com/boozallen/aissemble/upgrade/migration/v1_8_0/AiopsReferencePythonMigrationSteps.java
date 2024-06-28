package com.boozallen.aissemble.upgrade.migration.v1_8_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class AiopsReferencePythonMigrationSteps extends AbstractMigrationTest {

    private AbstractAissembleMigration migration;
    private File validatedFile;
    private static Boolean validateMigration(File original, File migrated) {
        Boolean isMigrated = false;

        try {
            isMigrated =  FileUtils.contentEquals(original, migrated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMigrated;
    }

    @Given("a Python implemented project references aiops_core_filestore.file_store_factory")
    public void aPythonImplementedProjectReferencesAiops_core_filestoreFile_store_factory() {
        testFile = getTestFile("v1_8_0/AiopsReferencePythonMigration/migration/Ingest.py");
    }

    @When("the 1.8.0 aiops reference python migration executes")
    public void theAiopsReferencePythonMigrationExecutes() {
        migration = new AiopsReferencePythonMigration();
        performMigration(migration);
    }

    @Then("the reference are updated to aissemble_core_filestore.file_store_factory")
    public void theReferenceAreUpdatedToAissemble_core_filestoreFile_store_factory() {
        validatedFile = getTestFile("/v1_8_0/AiopsReferencePythonMigration/validation/IngestValidated.py");
        assertTrue("Python modules are referencing an older package in imports.", validateMigration(testFile, validatedFile));
    }
}