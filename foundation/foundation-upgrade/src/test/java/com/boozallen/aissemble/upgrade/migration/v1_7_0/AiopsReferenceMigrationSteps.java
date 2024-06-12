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

import static junit.framework.TestCase.assertTrue;

public class AiopsReferenceMigrationSteps extends AbstractMigrationTest {
    private AbstractAissembleMigration migration;
    private File validatedFile;

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

    // Data Lineage migration scenarios:
    @Given("a Java file references the package com.boozallen.aiops.data.lineage")
    public void aJavaFileReferencesThePackageComBoozallenAiopsDataLineage() {
        testFile = getTestFile("v1_7_0/AiopsReferenceMigration/migration/CdiContainerFactory_Data_Lineage.java");
    }

    @When("the 1.7.0 aiops reference migration executes")
    public void theAiopsReferenceMigrationExecutes() {
        migration = new AiopsReferencesMigration();
        performMigration(migration);
    }

    @Then("the references are updated to com.boozallen.aissemble.data.lineage")
    public void theReferencesAreUpdatedToComBoozallenAissembleDataLineage() {
        validatedFile = getTestFile("/v1_7_0/AiopsReferenceMigration/validation/CdiContainerFactory_Data_Lineage_Validation.java");
        assertTrue("Data Lineage modules are references an older package in imports.", validateMigration(testFile, validatedFile));
    }

    // Foundation Core Java migration scenarios:
    @Given("a Java file references aiops in the foundation core java module package imports")
    public void aJavaFileReferencesAiopsInTheFoundationCoreJavaModulePackageImports() {
        testFile = getTestFile("v1_7_0/AiopsReferenceMigration/migration/CdiContainerFactory_Foundation_Core_Java.java");
    }

    @Then("the references are updated to point to aissmeble rather than aiops in foundation core java packages")
    public void theReferencesAreUpdatedToPointToAissmebleRatherThanAiopsInFoundationCoreJavaPackages() {
        validatedFile = getTestFile("/v1_7_0/AiopsReferenceMigration/validation/CdiContainerFactory_Foundation_Core_Java_Validation.java");
        assertTrue("Foundation Core Java modules are references an older package in imports.", validateMigration(testFile, validatedFile));
    }

    @Given("a Java file references in the .properties files")
    public void aJavaFileReferencesInThePropertiesFiles() {
        testFile = getTestFile("v1_7_0/AiopsReferenceMigration/migration/microprofile-config.properties");
    }

    @Then("the references within the .properties files are upgraded")
    public void theReferencesWithinThePropertiesFilesAreUpgraded() {
        validatedFile = getTestFile("/v1_7_0/AiopsReferenceMigration/validation/microprofile-config_validation.properties");
        assertTrue("Foundation Core Java modules are references an older package in imports.", validateMigration(testFile, validatedFile));
    }
}