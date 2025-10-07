package com.boozallen.aissemble.foundation;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Archetype
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

import groovy.lang.GroovyShell;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.maven.archetype.ArchetypeGenerationRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.*;

public class PostGenerateSteps {

    ArchetypeGenerationRequest request;
    Properties properties;
    Exception ex;

    GroovyShell groovyShell = new GroovyShell();

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(new File("target/" + request.getArtifactId()));

        properties = null;
        request = null;
        ex = null;
    }

    @Given("I am creating a project from foundation-archetype")
    public void iAmCreatingAProjectFromFoundationArchetype() {
        properties = new Properties();

        request = new ArchetypeGenerationRequest();
        request.setOutputDirectory("target");

        request.setProperties(properties);
    }

    @Given("the groupId is {string}")
    public void theGroupIdIs(String groupId) {
        request.setGroupId(groupId);
    }

    @Given("the artifactId is {string}")
    public void theArtifactIdIs(String artifactId) {
        request.setArtifactId(artifactId);
    }

    @Given("the version is {string}")
    public void theVersionIs(String version) {
        request.setVersion(version);
    }

    @Given("the package is {string}")
    public void thePackageIs(String arg0) {
        request.setPackage(arg0);
    }

    @Given("the projectGitUrl is {string}")
    public void theProjectGitUrlIs(String projectGitUrl) {
        properties.setProperty("projectGitUrl", projectGitUrl);
    }

    @When("the project is created from foundation-archetype")
    public void theProjectIsCreatedFromFoundationArchetype() throws IOException {

        Files.createDirectories(Paths.get("target/" + request.getArtifactId()));

        File pom = new File("target/" + request.getArtifactId() + "/pom.xml");
        pom.createNewFile();


        File script = new File("src/main/resources/META-INF/archetype-post-generate.groovy");
        groovyShell.setVariable("request", request);
        try {
            groovyShell.evaluate(script);
        } catch (Exception ex) {
            this.ex = ex;
        }
    }

    @Then("the project creation \"succeeds\" for invalid package name")
    public void theProjectCreationSucceedsForInvalidPackageName() {
        assertNull(ex);
    }

    @Then("the project creation \"fails\" for invalid package name")
    public void theProjectCreationFailsForInvalidPackageName() {
        assertNotNull("An Exception was expected", ex);
        assertTrue("Exception message did not begin with \"The provided package\"",
                this.ex.getMessage().trim().startsWith("The provided package"));
    }

}
