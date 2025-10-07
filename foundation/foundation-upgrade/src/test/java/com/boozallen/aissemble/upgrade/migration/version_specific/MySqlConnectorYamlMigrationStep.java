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

public class MySqlConnectorYamlMigrationStep extends AbstractMigrationTest {

    @Given("a spark-application values file that references mysql-connector-java in sparkApp.spec.deps.jars")
    public void sSparkApplicationValuesFileRefsToMySqlConnectorJavaInSparkAppDepsJars() {
        setTestFileToVersionMigration("MySqlConnectorYamlMigration", "values.yaml");
    }

    @Given("a spark-application values file that already references mysql-connector-j in spec.deps.jars")
    public void sSparkApplicationValuesFileRefsToMySqlConnectorJInSparkAppDepsJars() {
        setTestFileToVersionMigration("MySqlConnectorYamlMigration", "skip-values.yaml");
    }

    @When("the MySqlConnector yaml migration executes")
    public void mySqlConnectorYamlMigrationExecutes() {
        MySqlConnectorYamlMigration migration = new MySqlConnectorYamlMigration();
        performMigration(migration);
    }

    @Then("the mysql-connector-java jar coordinates are updated to mysql-connector-j jar")
    public void thePomFileIsUpdated() {
        assertTestFileMatchesExpectedFile("The values yaml file is not updated correctly");
    }

    @Then("the MySqlConnector migration is skipped")
    public void theMySqlConnectorMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
