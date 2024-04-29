package com.boozallen.aissemble.upgrade.migration;
/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.extensions.TiltfileMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.*;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

public class TiltMigrationSteps extends AbstractMigrationTest {

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is less than the project aiSSEMBLE version")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsLessThanTheProjectAiSSEMBLEVersion() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-3-0-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is greater than the project version")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsGreaterThanTheProjectVersion() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-5-0-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is equal to the project version")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsEqualToTheProjectVersion() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-aissemble-version-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is an older SNAPSHOT version than the project aiSSEMBLE version")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsAnOlderSNAPSHOTVersionThanTheProjectAiSSEMBLEVersion() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-3-0-SNAPSHOT-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE SNAPSHOT is greater than the project version")
    public void anExistingTiltfileWithVERSION_AISSEMBLESNAPSHOTIsGreaterThanTheProjectVersion() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-5-0-SNAPSHOT-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is less than the project aiSSEMBLE version in x.xx.xx format")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsLessThanTheProjectAiSSEMBLEVersionInXXxXxFormat() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-3-10-Tiltfile");
    }

    @Given("an existing Tiltfile with VERSION_AISSEMBLE is greater than the project aiSSEMBLE version in x.xx.xx format")
    public void anExistingTiltfileWithVERSION_AISSEMBLEIsGreaterThanTheProjectAiSSEMBLEVersionInXXxXxFormat() {
        testFile = getTestFile("TiltfileMigration/aissemble-version-1-5-30-Tiltfile");
    }

    @When("the Tiltfile version migration executes")
    public void theTiltfileVersionMigrationExecutes() {
        performMigration(new TiltfileMigrationTest());
    }

    @Then("the VERSION_AISSEMBLE is updated to the project version")
    public void theVERSION_AISSEMBLEIsUpdatedToTheProjectVersion() {
        assertTrue("Tiltfile VERSION AISSEMBLE does not line up with required aiSSEMBLE verision", successful);
    }

    @Then("the VERSION_AISSEMBLE should not be migrated")
    public void theVERSION_AISSEMBLEShouldNotBeMigrated() {
        assertFalse("Tiltfile VERSION AISSEMBLE is inline with the required aiSSEMBLE verision", shouldExecute);
    }

    @Then("the VERSION_AISSEMBLE is updated to the specified version value from the Baton specifications in the Tiltfile")
    public void theVERSION_AISSEMBLEIsUpdatedToTheSpecifiedVersionValueFromTheBatonSpecificationsInTheTiltfile() {
        assertTrue("Tiltfile VERSION AISSEMBLE does not line up with required aiSSEMBLE verision", shouldExecute);
    }
}
