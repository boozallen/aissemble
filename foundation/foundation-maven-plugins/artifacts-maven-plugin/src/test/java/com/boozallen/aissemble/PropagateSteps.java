package com.boozallen.aissemble;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;

/*-
 * #%L
 * artifacts-plugin Maven Mojo
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import java.util.Set;

import org.eclipse.aether.artifact.Artifact;
import java.util.List;
import java.util.ArrayList;

import org.apache.maven.project.MavenProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PropagateSteps extends MojoTestCaseWrapper {

    private String groupId;
    private String artifactId;
    private String version;
    private String url;
    private PropagateMojo propagateMojo;
    private Map<String, Artifact> artifacts = new HashMap<>();
    private PropagateAllMojo propagateAllMojo;
    private List<MavenProject> projects;

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

    @Given("the following artifacts:")
    public void the_following_artifacts(DataTable dataTable) throws Exception {
        List<Map<String, String>> dataMap = dataTable.asMaps(String.class, String.class);
        for(Map<String, String> entry : dataMap) {
            String classifier = entry.get("classifier");
            Optional<String> classifierOpt = Optional.empty();
            if(classifier != null) {
                classifierOpt = Optional.of(entry.get("classifier"));
            }
            artifacts.put(entry.get("name"), installStubArtifact(entry.get("groupId"), entry.get("artifactId"), entry.get("version"), entry.get("extension"), classifierOpt, getMojo("propagate", PropagateMojo.class)));
        }
    }

    @Given("a Maven groupId coordinate {word}")
    public void a_maven_group_id_coordinate(String groupId) {
        this.groupId = groupId;
    }
    @Given("a Maven artifactId coordinate {word}")
    public void a_maven_artifact_id_coordinate(String artifactId) {
        this.artifactId = artifactId;
    }
    @Given("a Maven version coordinate {word}")
    public void a_maven_version_coordinate(String version) {
        this.version = version;
    }
    @Given("a target Maven repository URL")
    public void a_target_maven_repository_url() {
        url = String.format("file://%s", tempRepository);
    }

    @When("the artifact propagation is triggered")
    public void the_artifact_propagation_is_triggered() throws Exception {
        propagateMojo = getMojo("propagate", PropagateMojo.class);
        propagateMojo.groupId = groupId;
        propagateMojo.artifactId = artifactId;
        propagateMojo.version = version;
        propagateMojo.url = url;
        propagateMojo.execute();
    }

    @Then("only artifacts {word} are deployed to an alternate repository")
    public void the_artifacts_deployed_to_an_alternate_repository(String keys) throws Exception {
        artifactsInRepoCheck(tempRepository, getArtifactsFromKeys(keys), true);
        artifactsInRepoCheck(tempRepository, getArtifactsNotFromKeys(keys), false);
    }

    @Given("a {word} {word} with pom dependency on {word}")
    public void a_module_with_pom_dependency_on(String mode, String moduleName, String coordinates) {
        if(mode.equals("project")) {
            projects = new ArrayList<>();
        }
        MavenProject project = new MavenProject();
        project.setArtifacts(getArtifacts(coordinates));
        projects.add(project);
    }

    @When("the project dependency propagation is triggered")
    public void the_project_dependency_propagation_is_triggered() throws Exception {
        propagateAllMojo = getMojo("propagate-all", PropagateAllMojo.class);
        propagateAllMojo.mavenSession.setProjects(projects);
        propagateAllMojo.url = url;
        propagateAllMojo.execute();
    }

    /**
     * Gets the artifacts from the comma seperated key
     * @param keys
     * @return
     */
    private List<Artifact> getArtifactsFromKeys(String keys) {
        List<Artifact> installedArtifacts = new ArrayList<>();
        String[] splitKeys = keys.split(",");
        for(String key : splitKeys) {
            installedArtifacts.add(artifacts.get(key));
        }
        return installedArtifacts;
    }

    private List<Artifact> getArtifactsNotFromKeys(String keys) {
        List<Artifact> notInstalledArtifacts = new ArrayList<>();
        Set<String> artifactKeys = artifacts.keySet();
        Set<String> splitKeys = new HashSet<>(List.of(keys.split(",")));
        artifactKeys.removeAll(splitKeys);
        for(String key : artifactKeys) {
            notInstalledArtifacts.add(artifacts.get(key));
        }
        return notInstalledArtifacts;
    }

    private Set<org.apache.maven.artifact.Artifact> getArtifacts(String coordinates) {
        String[] colonSplit = coordinates.split(":");
        String groupId = colonSplit[0];
        String artifactId = colonSplit[1];
        String version = colonSplit[2];
        Set<org.apache.maven.artifact.Artifact> artifactList = new HashSet<>();
        for(String key : artifacts.keySet()) {
            Artifact artifact = artifacts.get(key);
            if(artifact.getGroupId().equals(groupId) && artifact.getArtifactId().equals(artifactId) && artifact.getVersion().equals(version)) {
                org.apache.maven.artifact.Artifact newArtifact = new org.apache.maven.artifact.DefaultArtifact(groupId, artifactId, version, "test", artifact.getExtension(), artifact.getClassifier(), null);
                newArtifact.setFile(artifact.getFile());
                artifactList.add(newArtifact);
            }
        }
        return artifactList;
    }

}
