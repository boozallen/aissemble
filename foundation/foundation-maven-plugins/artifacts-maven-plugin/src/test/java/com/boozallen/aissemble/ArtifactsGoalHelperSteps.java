package com.boozallen.aissemble;

/*-
 * #%L
 * propagate-artifacts-plugin Maven Mojo
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import java.util.List;
import java.util.ArrayList;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import java.util.Optional;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ArtifactsGoalHelperSteps extends MojoTestCaseWrapper {

    private Artifact artifact;
    private Optional<Artifact> result;
    private ArtifactsGoalHelper artifactsGoalHelper;
    private List<Artifact> artifacts;
    private String url;
    private static String tempRepository;

    @Before
    public void setup() {
        tempRepository = TEMP_DIR_PATH;
        createDir(tempRepository); 
    }

    @After
    public void cleanup() throws Exception {
        removeDir(tempRepository);
    }

    @Given("Maven coordinates to a pom artifact that is locally installed")
    public void maven_coordinates_to_a_pom_artifact_that_is_locally_installed() throws Exception {
        artifactsGoalHelper = new ArtifactsGoalHelper(getMojo("propagate", PropagateMojo.class));
        this.artifact = new DefaultArtifact("org.codehaus.plexus", "plexus-utils", "pom", "3.3.1");// This is a dependency of the plugin
    }

    @Given("Maven coordinates to a pom artifact that is not locally installed")
    public void maven_coordinates_to_a_pom_artifact_that_is_not_locally_installed() throws Exception {
        artifactsGoalHelper = new ArtifactsGoalHelper(getMojo("propagate", PropagateMojo.class));
        artifact = new DefaultArtifact("com.fake", "artifact", "pom", "1.3.3");
    }

    @When("the local repository is queried for the artifact")
    public void the_local_repository_is_queried_for_the_artifact() {
        result = artifactsGoalHelper.getInstalledArtifact(artifact);
    }
    @Then("The artifact is found to not exist")
    public void the_artifact_is_found_to_not_exist() {
        assertTrue(!result.isPresent());
    }

    @Then("The artifact is found to exist")
    public void the_artifact_is_found_to_exist() {
        assertTrue(result.isPresent());
    }

    @Given("locally installed artifacts")
    public void locally_installed_artifact() throws Exception {
        // Write code here that turns the phrase above into concrete actions
        PropagateMojo mojo = getMojo("propagate", PropagateMojo.class);
        url = String.format("file://%s", tempRepository);
        artifactsGoalHelper = new ArtifactsGoalHelper(mojo);
        artifacts = new ArrayList<>();
        artifact = installStubArtifact("com.example", "test", "1.0.0", "jar", Optional.empty(), mojo);
        artifacts.add(artifact);    // Used for next step in getting the coordinates
        artifacts.add(installStubArtifact("com.example", "test", "1.0.0", "pom", Optional.empty(), mojo));
        artifacts.add(installStubArtifact("com.example", "test", "1.0.0", "jar", Optional.of("tests"), mojo));
        artifacts.add(installStubArtifact("com.example", "test", "1.0.0", "jar", Optional.of("sources"), mojo));
        artifacts.add(installStubArtifact("com.example", "test", "1.0.0", "jar", Optional.of("javadoc"), mojo));
        artifacts.add(installStubArtifact("com.example", "test", "1.0.0", "war", Optional.empty(), mojo));
    }

    @When("the artifacts deployment process is triggered")
    public void the_artifact_deployment_process_is_triggered() throws Exception {
        artifactsGoalHelper.deployArtifacts(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion(), url, "");
    }

    @Then("the artifacts are deployed to an alternate repository")
    public void the_artifact_are_deployed_to_an_alternate_repository() throws Exception {
        artifactsInRepoCheck(tempRepository, artifacts, true);
    }

}
