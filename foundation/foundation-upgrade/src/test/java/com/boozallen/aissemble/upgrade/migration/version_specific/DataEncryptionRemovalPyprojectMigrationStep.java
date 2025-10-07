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

public class DataEncryptionRemovalPyprojectMigrationStep extends AbstractMigrationTest {

    @Given("a Pyproject file that has the data encryption dependencies")
    public void aPyprojectThatHasTheDataEncryptionDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPyprojectMigration", "pyproject.toml");
    }

    @Given("a Pyproject file that does not have the data encryption dependencies")
    public void aPyprojectThatDoesNotHaveTheDataDependencies() {
        setTestFileToVersionMigration("DataEncryptionRemovalPyprojectMigration", "skip-pyproject.toml");
    }

    @When("the 1.13.0 data encryption removal pyproject migration executes")
    public void DataEncryptionRemovalPyprojectMigrationExecutes() {
        DataEncryptionRemovalPyprojectMigration migration = new DataEncryptionRemovalPyprojectMigration();
        performMigration(migration);
    }

    @Then("the data encryption dependencies are removed from the Pyproject file")
    public void theDataEncryptionDependenciesAreRemovedFromPyproject() {
        assertTestFileMatchesExpectedFile("Data encryption dependencies have been removed from Pyproject file");
    }

    @Then("the data encryption removal pyproject migration is skipped")
    public void theDataEncryptionRemovalMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
