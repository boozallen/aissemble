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

public class MavenBuildCacheMigrationSteps extends AbstractMigrationTest {

    @Given("a Maven build cache config with max builds set higher than one")
    public void aMavenBuildCacheConfigWithMaxBuildsSetHigherThanOne() {
        setTestFileToVersionMigration("MavenBuildCacheMigration", "multiple-cached-builds.xml");
    }

    @Given("a Maven build cache config with max builds set to one")
    public void aMavenBuildCacheConfigWithMaxBuildsSetToOne() {
        setTestFileToVersionMigration("MavenBuildCacheMigration", "one-max-build.xml");
    }
    @When("the 1.13.0 Maven build cache migration executes")
    public void theMavenBuildCacheMigrationExecutes() {
        performMigration(new MavenBuildCacheMigration());
    }

    @Then("the config is updated to set max builds to one")
    public void theConfigIsUpdatedToSetMaxBuildsTo() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("<maxBuilds> not correctly updated to 1!");
    }

    @Then("the build cache migration is skipped")
    public void theBuildCacheMigrationIsSkipped() {
        assertMigrationSkipped();
    }

}
