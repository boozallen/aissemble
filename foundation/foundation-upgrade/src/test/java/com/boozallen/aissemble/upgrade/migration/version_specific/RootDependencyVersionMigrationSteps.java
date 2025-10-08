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

import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class RootDependencyVersionMigrationSteps extends AbstractMigrationTest {
    RootDependencyVersionMigration migration = new RootDependencyVersionMigration();

    @Given("a project with hardcoded aissemble versions in the root pom")
    public void aProjectWithHardcodedAissembleVersionsInTheRootPom() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("RootDependencyVersionMigration", "pom.xml");
        migration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("a project with aissemble versions using the maven property in the root pom")
    public void aProjectWithAissembleVersionsUsingTheMavenPropertyInTheRootPom() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("RootDependencyVersionMigration", "pom-with-version-property.xml");
        migration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @When("the dependency version migration is executed")
    public void theDependencyVersionMigrationIsExecuted() {
        performMigration(migration);
    }

    @Then("the aissemble versions are update to use the maven property")
    public void theAissembleVersionsAreUpdateToUseTheMavenProperty() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Version was not replaced with the maven property");
    }

    @Then("the dependency version migration is skipped")
    public void theDependencyVersionMigrationIsSkipped() {
        assertMigrationSkipped();
    }
}
