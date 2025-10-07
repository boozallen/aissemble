package com.boozallen.aissemble.upgrade.migration;

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

import com.boozallen.aissemble.upgrade.migration.extensions.HelmfileVersionMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmFileVersionMigrationSteps extends AbstractMigrationTest {

    @Given("a projects helmfile values has an out of date aissemble version")
    public void aProjectsHelmfileValuesHasAnOutOfDateAissembleVersion() {
        setTestFileToBaseMigration("HelmfileVersionMigration", "values-helmfile.yaml.gotmpl");
    }

    @Given("a projects helmfile values has the desired aissemble version")
    public void aProjectsHelmfileValuesHasTheDesiredAissembleVersion() {
        setTestFileToBaseMigration("HelmfileVersionMigration", "values-skip-helmfile.yaml.gotmpl");
    }

    @When("the helmfile version migration is performed")
    public void theHelmfileVersionMigrationIsPerformed() {
        performMigration(new HelmfileVersionMigrationTest());
    }

    @Then("the helmfile value is update")
    public void theHelmfileValueIsUpdate() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("aiSSEMBLE version for the helmfile values was not correctly updated");
    }

    @Then("the migration is not performed")
    public void theMigrationIsNotPerformed() {
        assertMigrationSkipped();
    }
}
