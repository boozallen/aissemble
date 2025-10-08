package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.notification.Notification;
import org.technologybrewery.fermenter.mda.notification.NotificationCollector;

import com.boozallen.aiops.mda.metamodel.element.AbstractModelInstanceSteps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelmfileManualActionsSteps extends AbstractModelInstanceSteps {

    private static final Logger logger = LoggerFactory.getLogger(HelmfileManualActionsSteps.class);
    private String projectName = null;

    @Before("@helmfile")
    public void setup(Scenario scenario) throws IOException {
        this.scenario = scenario.getName();
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @After("@helmfile")
    public void teardown() throws IOException {
        FileUtils.deleteDirectory(GENERATED_METADATA_DIRECTORY);
    }

    @Given("a project with name {string} exists with a data-delivery pipeline")
    public void aProjectWithNameExampleExists(String projectName) throws IOException {
        this.projectName = projectName;
        createProject(projectName, "shared");
        // Create an initial helmfile
        createHelmfile(true);
        createHelmfile(false);
        // Create pipeline mda
        createAndSavePipeline("ExampleSparkPipeline", "data-flow", "data-delivery-spark");
    }

    @When("the {string} is generated")
    public void theIsGenerated(String profile) throws Exception {
        readMetadata(profile);
        Map<String, ExpandedProfile> profiles = loadProfiles();
        GenerateSourcesHelper.performSourceGeneration(profile, profiles, this::createGenerationContext, (missingProfile, foundProfiles) -> {
            throw new RuntimeException("Missing profile: " + missingProfile);
        }, new Slf4jDelegate(logger), projectDir.toFile());
    }

    @Then("the manual action to add the helmfile release is displayed")
    public void theManualActionToAddTheHelmfileChartReleaseIsDisplayed() {
        Map<String, Map<String, Notification>> notifications = NotificationCollector.getNotifications();

        String file = this.projectDir.resolve("helmfile-apps.yaml.gotmpl").toString();
        assertTrue("No notifications for helmfile.", notifications.containsKey(file));
        Map<String, Notification> fileNotifications = notifications.get(file);
        assertTrue("Failed to generate the helmfile release notification.", fileNotifications.containsKey(
                "helmfile-apps_release_" + this.projectName));
    }

    @Then("the manual action to add the helmfile release for the pipeline is displayed")
    public void theManualActionToAddTheHelmfileChartReleaseForThePipelineIsDisplayed() {
        Map<String, Map<String, Notification>> notifications = NotificationCollector.getNotifications();

        String file = this.projectDir.resolve("helmfile.yaml.gotmpl").toString();
        assertTrue("No notifications for helmfile.", notifications.containsKey(file));
        Map<String, Notification> fileNotifications = notifications.get(file);
        assertTrue("Failed to generate the helmfile release notification for the pipeline.",
                fileNotifications.containsKey(
                "helmfile_release_example-spark-pipeline"));
    }

    @Then("the ArgoCD template is not created")
    public void theArgoCDTemplateIsNotCreated() {
        Path templatesDir = this.projectDir.resolve("main/resources/templates");
        assertTrue("Failed to verify ArgoCD template not created.", Files.notExists(templatesDir));
    }
}
