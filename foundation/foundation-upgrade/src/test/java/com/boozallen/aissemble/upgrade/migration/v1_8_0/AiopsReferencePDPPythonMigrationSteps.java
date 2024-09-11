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

public class AiopsReferencePDPPythonMigrationSteps extends AbstractMigrationTest {

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

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return isFileNameSame - Boolean indicating whether the file names are the same.
     */

    public static Boolean isAissembleSecurityProperties(File file) {
        return file.getName().equals("aissemble-security.properties");
    }

    @Given("a Python implemented project policy decision point docker properties file is referencing aiops")
    public void aPythonImplementedProjectPolicyDecisionPointDockerPropertiesFileIsReferencingAiops() {
        testFile = getTestFile("v1_8_0/AiopsReferencePDPPythonMigration/migration/aiops-security.properties");
    }

    @When("the 1.8.0 aiops reference pdp python migration executes")
    public void theAiopsReferencePdpPythonMigrationExecutes() {
        migration = new AiopsReferencePDPPythonMigration();
        performMigration(migration);
    }

    @Then("the properties file will be renamed to aissemble-security.properties")
    public void thePropertiesFileWillBeRenamedToAissembleSecurityProperties() {
        assertTrue("aissemble-securities.properties file was unabled to be renamed.", isAissembleSecurityProperties(getTestFile("v1_8_0/AiopsReferencePDPPythonMigration/migration/aissemble-security.properties")));
    }

    @Given("a Python implemented project policy decision point docker Dockerfile is referencing aiops in the target path of the properties file")
    public void aPythonImplementedProjectPolicyDecisionPointDockerDockerfileIsReferencingAiopsInTheTargetPathOfThePropertiesFile() {
        testFile = getTestFile("v1_8_0/AiopsReferencePDPPythonMigration/migration/Dockerfile");
    }

    @Then("the target path in the Dockerfile will be refactored to referenced aissemble-security.properties")
    public void theTargetPathInTheDockerfileWillBeRefactoredToReferencedAissembleSecurityProperties() {
        validatedFile = getTestFile("v1_8_0/AiopsReferencePDPPythonMigration/validation/Dockerfile_Validated");
        assertTrue("PDP Dockerfile is still referencing aiops in the target path.", validateMigration(testFile, validatedFile));
    }
}