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

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.nio.file.Path;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.extensions.HelmfileDeploymentScriptMigrationTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmfileDeploymentScriptMigrationSteps extends AbstractMigrationTest {

    @Given("a projects has a deployment script")
    public void aProjectsHasADeploymentScript() {
        setTestFileToVersionMigration("HelmfileDeploymentScriptMigration", "JenkinsfileDeploy.groovy");
    }

    @Given("a projects has a jenkins pipeline steps file")
    public void aProjectsHasAJenkinsPipelineStepsFile() {
        setTestFileToVersionMigration("HelmfileDeploymentScriptMigration", "jenkinsPipelineSteps.groovy");
    }

    @Given("the helmfile deployment migration is set to run")
    public void theSystemPropertyActivationKeyIsSet() {
        System.setProperty("aissemble.enable.helmfile.migration", "true");
    }

    @When("the helmfile deployment script migration is performed")
    public void theHelmfileDeploymentScriptMigrationIsPerformed() {
        HelmfileDeploymentScriptMigrationTest helmfileDeploymentScriptMigration = new HelmfileDeploymentScriptMigrationTest();
        performMigration(helmfileDeploymentScriptMigration);
    }

    @Then("the deploy file is updated")
    public void theDeployFileIsUpdated() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("JenkinsfileDeploy.groovy was not updated correctly");
    }

    @Then("the jenkins Pipeline steps file is deleted")
    public void jenkinsPipelineStepsFileIsDeleted() {
        File jenkinsPipelineSteps = getTestFile(Path.of("version-specific", "HelmfileDeploymentScriptMigration", "migration",
                "jenkinsPipelineSteps.groovy").toString());
        assertFalse("jenkinsPipelineSteps was not successfully deleted.", jenkinsPipelineSteps.exists());
    }
}
