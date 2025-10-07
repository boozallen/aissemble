package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DataEncryptionRemovalPomMigrationStep extends AbstractMigrationTest {

    @Given("a POM file that has the data encryption dependencies")
    public void aPomFileThatHasTheDataEncryptionDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPomMigration", "pom.xml");
    }

    @Given("a POM file that does not have the data encryption dependencies")
    public void aPomFileThatDoesNotHaveTheDataDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPomMigration", "skip-pom.xml");
    }

    @When("the 1.13.0 data encryption removal pom migration executes")
    public void DataEncryptionRemovalPomMigrationExecutes() {
        DataEncryptionRemovalPomMigration migration = new DataEncryptionRemovalPomMigration();
        performMigration(migration);
    }

    @Then("the data encryption dependencies are removed from the POM file")
    public void theDataEncryptionDependenciesAreRemovedFromPomFile() {
        assertTestFileMatchesExpectedFile("Data encryption dependencies have been removed from POM file");
    }

    @Then("the data encryption removal pom migration is skipped")
    public void theDataEncryptionRemovalMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
