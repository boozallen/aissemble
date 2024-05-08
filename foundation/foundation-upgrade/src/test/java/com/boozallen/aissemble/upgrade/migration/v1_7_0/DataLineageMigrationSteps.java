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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;

import static junit.framework.TestCase.assertTrue;
import static org.apache.commons.io.FileUtils.contentEqualsIgnoreEOL;

public class DataLineageMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;
    private File validatedFile = getTestFile("/v1_7_0/DataLineageMigration/validation/CdiContainerFactory_Validation.java");

    /**
     * Function to check whether 2 given files are the same.
     * @param original first file to compare
     * @param migrated second file to compare
     * @return shouldExecute - whether the migration is necessary.
     */
    private static Boolean validateMigration(File original, File migrated) {
        Boolean isMigrated = false;

        try {
            isMigrated =  FileUtils.contentEquals(original, migrated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMigrated;
    }

    @Given("a Java file references the package com.boozallen.aiops.data.lineage")
    public void aJavaFileReferencesThePackageComBoozallenAiopsDataLineage() {}

    @When("the 1.7.0 data lineage package migration executes")
    public void theDataLineagePackageMigrationExecutes() {
        migration = new DataLineageAiopsReferencesMigration();
        testFile = getTestFile("v1_7_0/DataLineageMigration/migration/CdiContainerFactory.java");
        performMigration(migration);
    }

    @Then("the references are updated to com.boozallen.aissemble.data.lineage")
    public void theReferencesAreUpdatedToComBoozallenAissembleDataLineage() {
        assertTrue("Data Lineage modules are references an older package in imports.", validateMigration(testFile, validatedFile));
    }


}