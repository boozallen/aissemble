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

public class AiopsReferenceJavaMigrationSteps extends AbstractMigrationTest {
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

    // Data Access Scenarios
    @Given("a Java file references the package com.boozallen.aiops.data.access")
    public void aJavaFileReferencesThePackageComBoozallenAiopsDataAccess() {
        testFile = getTestFile("v1_8_0/AiopsReferenceJavaMigration/migration/DataAccessRecord.java");
    }

    @When("the 1.8.0 aiops reference java migration executes")
    public void theAiopsReferenceJavaMigrationExecutes() {
        migration = new AiopsReferenceJavaMigration();
        performMigration(migration);
    }

    @Then("the references are updated to com.boozallen.aissemble.data.access")
    public void theReferencesAreUpdatedToComBoozallenAissembleDataAccess() {
        validatedFile = getTestFile("/v1_8_0/AiopsReferenceJavaMigration/validation/DataAccessRecordValidated.java");
        assertTrue("Data Access is still referencing aiops in the java package.", validateMigration(testFile, validatedFile));
    }
}
