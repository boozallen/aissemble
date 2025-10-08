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

public class SparkProvidedDependenciesMigrationStep extends AbstractMigrationTest {

    @Given("a POM contains extensions-data-delivery-spark dependency and {string} hive, spark, hadoop dependencies")
    public void aPomContainsExtensionsDataDeliverySparkDependencyAndPossibleHiveSparkHadoopDeps(String possible) {
        String fileName = possible.equals("some")? "some-": "";
        setTestFileToVersionMigration("SparkProvidedDependenciesMigration", String.format("%spom.xml", fileName));
    }

    @Given("a POM has no extensions-data-delivery-spark dependency")
    public void aPomFileHasNoHiveSparkHadoopDependencies() {
        setTestFileToVersionMigration("SparkProvidedDependenciesMigration", "skip-pom.xml");
    }

    @Given("a POM with extensions-data-delivery-spark, and hive, spark, hadoop dependencies defined correctly")
    public void aPomFileHasExpectedHiveSparkHadoopDependencies() {
        setTestFileToVersionMigration("SparkProvidedDependenciesMigration", "migrated-pom.xml");
    }

    @When("the 1.13.0 spark pipeline jar dependencies migration executes")
    public void DataEncryptionRemovalPomMigrationExecutes() {
        SparkProvidedDependenciesMigration migration = new SparkProvidedDependenciesMigration();
        performMigration(migration);
    }

    @Then("the pom file is updated")
    public void thePomFileIsUpdated() {
        assertTestFileMatchesExpectedFile("The Pom file is not updated correctly");
    }

    @Then("the spark pipeline jar dependencies migration is skipped")
    public void theSparkProvidedDependenciesMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
