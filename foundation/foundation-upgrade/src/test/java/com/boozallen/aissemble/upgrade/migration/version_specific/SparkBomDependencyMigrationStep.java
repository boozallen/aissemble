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
import com.boozallen.aissemble.upgrade.migration.extensions.SparkBomDependencyMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class SparkBomDependencyMigrationStep extends AbstractMigrationTest {

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency")
    public void aPomFileWithoutAissembleSparkBom() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom.xml");
    }

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency with no spark dependency")
    public void aPomFileWithoutAissembleSparkBomAndNoSparkDependency() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom-no-spark.xml");
    }

    @Given("a POM file that doesn't have the aissemble-spark-bom dependency with an existing dependencyManagement section")
    public void aPomFileWithoutAissembleSparkBomAndExistingDependencyManagementSection() {
       setTestFileToVersionMigration("SparkBomDependencyMigration", "pom-dependency-management-exists.xml");
    }

    @Given("contains a spark dependency")
    public void containsSparkDependency() {
        // Already covered by the test POM file content
    }

    @Given("includes aissemble build-parent in its hierarchy")
    public void includesAissembleBuildParent() {
        // Already covered by the test POM file content
    }

    @When("the aissemble-spark-bom pom migration executes")
    public void runMigration() {
        performMigration(new SparkBomDependencyMigrationTest(testFile));
    }

    @Then("the aissemble-spark-bom dependency is added to the POM file")
    public void aissembleSparkBomAdded() {
        assertTestFileMatchesExpectedFile("aissemble-spark-bom dependencyManagement block was not added as expected");
    }

    @Given("doesn't contain a spark dependency")
    public void doesNotContainSparkDependency() {
        // Already covered by the test POM file content
    }

    @Then("the migration is skipped")
    public void migrationIsSkipped() {
        assertMigrationSkipped();
    }
}
