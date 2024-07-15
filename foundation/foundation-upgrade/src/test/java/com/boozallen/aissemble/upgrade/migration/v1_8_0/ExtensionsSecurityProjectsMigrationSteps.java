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

public class ExtensionsSecurityProjectsMigrationSteps extends AbstractMigrationTest {

    private AbstractAissembleMigration migration;
    private File validatedFile;

    private static Boolean validateMigration(File original, File migrated) {
        Boolean isMigrated = false;

        try {
            isMigrated = FileUtils.contentEquals(original, migrated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMigrated;
    }

    @Given("a Java file references an object with the old object name with aiops")
    public void aJavaFileReferencesAnObjectWithTheOldObjectNameWithAiops() {
        testFile = getTestFile("v1_8_0/ExtensionsSecurityProjectsMigration/migration/Ingest.java");
    }

    @When("the 1.8.0 aiops reference extension security migration executes")
    public void theAiopsReferenceExtensionSecurityMigrationExecutes() {
        migration = new ExtensionsSecurityProjectsMigration();
        performMigration(migration);
    }

    @Then("the objects are updated to aissemble replacing aiops")
    public void theObjectsAreUpdatedToAissembleReplacingAiops() {
        validatedFile = getTestFile("/v1_8_0/ExtensionsSecurityProjectsMigration/validation/IngestValidation.java");
        assertTrue("Extensions Security projects are still referencing aiops in the java Objects.", validateMigration(testFile, validatedFile));
    }
}


