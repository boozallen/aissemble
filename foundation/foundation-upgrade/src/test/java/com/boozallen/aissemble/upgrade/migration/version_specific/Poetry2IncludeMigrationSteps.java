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

public class Poetry2IncludeMigrationSteps extends AbstractMigrationTest {
    @Given("a pyproject.toml file that uses includes to package Fermenter-generated files")
    public void aPyprojectTomlFileThatUsesIncludesToPackageFermenterGeneratedFiles() {
        setTestFileToVersionMigration("Poetry2IncludeMigration", "pyproject.toml");
    }

    @When("the poetry 2 include migration is executed")
    public void thePoetryIncludeMigrationIsExecuted() {
        Poetry2IncludeMigration migration = new Poetry2IncludeMigration();
        performMigration(migration);
    }

    @Then("the configuration is updated to explicitly list the formats")
    public void theConfigurationIsUpdatedToExplicitlyListTheFormats() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("The pyproject.toml file is not updated correctly");
    }
}
