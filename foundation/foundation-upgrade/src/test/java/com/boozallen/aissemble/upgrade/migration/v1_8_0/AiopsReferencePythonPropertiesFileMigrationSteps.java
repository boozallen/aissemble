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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class AiopsReferencePythonPropertiesFileMigrationSteps extends AbstractMigrationTest {
    public static final Logger logger = LoggerFactory.getLogger(AiopsReferencePythonPropertiesFileMigrationSteps.class);

    private AbstractAissembleMigration migration;
    private File validatedFile;

    private static Boolean validateMigration(File original, File migrated) {
        Boolean isMigrated = false;

        try {
            isMigrated =  FileUtils.contentEquals(original, migrated);
        } catch (Exception e) {
            logger.error("Error in determining there was a successful migration.");
        }
        return isMigrated;
    }

    // Updating the values within the aissemble-security.properties file
    @Given("a Python implemented projects policy decision point docker properties file is referencing aiops")
    public void aPythonImplementedProjectsPolicyDecisionPointDockerPropertiesFileIsReferencingAiops() {
        testFile = getTestFile("v1_8_0/AiopsReferencePythonPropertiesFileMigration/migration/aissemble-security.properties");
    }

    @When("the 1.8.0 aiops reference python properties file migration executes")
    public void theAiopsReferencePythonPropertiesFileMigrationExecutes() {
        migration = new AiopsReferencePDPPythonMigration();
        performMigration(migration);
    }

    @Then("the values within the properties file will be updated to reference aissemble instead of aiops")
    public void theValuesWithinThePropertiesFileWillBeUpdatedToReferenceAissembleInsteadOfAiops() {
        validatedFile = getTestFile("/v1_8_0/AiopsReferencePythonPropertiesFileMigration/validation/aissemble-security_validated.properties");
        assertTrue("policy decision point docker properties file values are still referencing aiops.", validateMigration(testFile, validatedFile));
    }

    // Updating the Dockerfile with new aissemble-security.properties file name reference in target path.
    @Given("a Python implemented projects policy decision point docker Dockerfile is referencing an aiops in the properties file target path")
    public void aPythonImplementedProjectsPolicyDecisionPointDockerDockerfileIsReferencingAnAiopsInThePropertiesFileTargetPath() {
        testFile = getTestFile("v1_8_0/AiopsReferencePythonPropertiesFileMigration/migration/Dockerfile");
    }

    @Then("the target path will be updated to reference the renamed aissemble-security.properties path name")
    public void theTargetPathWillBeUpdatedToReferenceTheRenamedAissembleSecurityPropertiesPathName() {
        validatedFile = getTestFile("/v1_8_0/AiopsReferencePythonPropertiesFileMigration/validation/Dockerfile_Validated");
        assertTrue("policy decision point docker Dockerfile is referencing aiops in the target path.", validateMigration(testFile, validatedFile));
    }


//    // Refactoring aiops-security.properties file
//    @Given("a Python implemented project generates a new properties file after the 1.8.0 upgrade and the aiops-security.properties still exists")
//    public void aPythonImplementedProjectGeneratesANewPropertiesFileAfterTheUpgradeAndTheAiopsSecurityPropertiesStillExists() {
//        testFile = getTestFile("v1_8_0/AiopsReferencePythonPropertiesFileMigration/migration/aiops-security.properties");
//    }
//
//    @Then("the existing aiops-security.properties will be renamed to aissemble-security.properties")
//    public void theExistingAiopsSecurityPropertiesWillBeRenamedToAissembleSecurityProperties() {
//        validatedFile = getTestFile("/v1_8_0/AiopsReferencePythonPropertiesFileMigration/validation/aissemble-security.properties");
//        assertTrue("policy decision point docker properties file is referencing aiops in the file name.", validateMigration(testFile, validatedFile));
//    }
}
