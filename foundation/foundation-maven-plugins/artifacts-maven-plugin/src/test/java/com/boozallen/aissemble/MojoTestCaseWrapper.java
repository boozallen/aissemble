package com.boozallen.aissemble;

/*-
 * #%L
 * MDA Maven::Plugin
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.execution.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import java.util.Optional;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import java.nio.file.Paths;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.model.Model;
import java.io.FileWriter;

import org.apache.maven.model.Dependency;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;

import org.apache.commons.io.FileUtils;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import java.util.Collection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wraps the default behavior provided by the Maven plugin testing harness through {@link AbstractMojoTestCase} to
 * set standard Maven defaults on any {@link Mojo}s that are created and reduce the amount of mock stubs required.
 * <p>
 * This class is largely adapted from the testing approach developed by the license-audit-maven-plugin's
 * {@code BetterAbstractMojoTestCase} (https://github.com/ahgittin/license-audit-maven-plugin)
 */
public class MojoTestCaseWrapper extends AbstractMojoTestCase {
    private static final String LOCAL_DIR = Paths.get("").toAbsolutePath().toString();
    static final String TEMP_DIR_PATH = Paths.get(LOCAL_DIR, "tempDir").toString();
    static final String POM_DIR = Paths.get(LOCAL_DIR, "src", "test", "resources", "pom").toString();
    static final String TEST_POM_PATH = Paths.get(POM_DIR, "pom.xml").toString();
    static String tempRepository;
    
    private MavenSession session;
    public void configurePluginTestHarness() throws Exception {
        super.setUp();
    }

    /**
     * Creates and appropriately configures the {@link Mojo} responsible for executing the given
     * plugin goal as defined within the given {@code pom.xml} {@link File}.
     *
     * @param pom  {@code pom.xml} file defining desired plugin and configuration to test.
     * @param goal target plugin goal for which to create the associated {@link Mojo}
     * @return
     * @throws Exception
     */
    public Mojo lookupConfiguredMojo(File pom, String goal) throws Exception {
        assertNotNull(pom);
        assertTrue(pom.exists());
        this.session = newMavenSession();
        ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();
        return lookupConfiguredMojo(project, goal);
    }

    /**
     * Creates a new {@link MavenSession} with standard defaults populated.
     *
     * @return
     * @throws Exception
     */
    public MavenSession newMavenSession() throws Exception {
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        MavenExecutionResult result = new DefaultMavenExecutionResult();

        // Populates sensible defaults, including repository basedir and remote repos
        MavenExecutionRequestPopulator populator = getContainer().lookup(MavenExecutionRequestPopulator.class);
        populator.populateDefaults(request);

        // Enables the usage of Java system properties for interpolation and profile activation
        request.setSystemProperties(System.getProperties());

        // Ensures that the repo session in the maven session
        // has a repo manager and it points at the local repo
        DefaultMaven maven = (DefaultMaven) getContainer().lookup(Maven.class);
        DefaultRepositorySystemSession repoSession =
                (DefaultRepositorySystemSession) maven.newRepositorySession(request);
        repoSession.setLocalRepositoryManager(
                new SimpleLocalRepositoryManagerFactory().newInstance(repoSession,
                        new LocalRepository(request.getLocalRepository().getBasedir())));

        MavenSession session = new MavenSession(getContainer(),
                repoSession,
                request, result);

        return session;

    }

    /**
     * Extends the parent implementation of this method to delegate to the new {@link #newMavenSession()} introduced
     * in {@link MojoTestCaseWrapper}, which sets the defaults that are normally expected by Maven
     */
    @Override
    protected MavenSession newMavenSession(MavenProject project) {
        MavenSession session;
        try {
            session = this.session;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        session.setCurrentProject(project);
        session.setProjects(Arrays.asList(project));
        return session;
    }

    /**
     * Gets a mojo using the default pom.xml file. 
     * 
     * @param goal The plugins goal to call. 
     * @param classType The classtype of the plugin. 
     * @return The Maven mojo. 
     * @throws Exception. 
     */
    <T> T getMojo(String goal, Class<T> classType) throws Exception {
        return getMojo(goal, classType, TEST_POM_PATH);
    }

    /**
     * Gets a mojo using the given pom path. 
     * 
     * @param goal The plugins goal to call. 
     * @param classType The classtype of the plugin. 
     * @param pomPath The path to the pom file to inject in the plugin. 
     * @return The Maven mojo. 
     * @throws Exception. 
     */
    <T> T getMojo(String goal, Class<T> classType, String pomPath) throws Exception {
        configurePluginTestHarness();
        File pomFile = new File(pomPath);
        T mojo = classType.cast(lookupConfiguredMojo(pomFile, goal));
        assertNotNull(mojo);
        return mojo;
    }

    /**
     * Gets the path from the given coordinates from the repo url. 
     * 
     * @param repoUrl The repo's url. 
     * @param groupId The group ID to add to the path. 
     * @param artifactId The artifact ID to add to the path. 
     * @param version The version ID to add to the path. 
     * @return The path to the coordinates. 
     */
    private static String getPathFromCoordinates(String repoUrl, String groupId, String artifactId, String version) {
        String[] directories = groupId.split("\\.");
        assertTrue(directories.length > 0);
        String currentPath = repoUrl;
        for(String directory : directories) {
            currentPath = Paths.get(currentPath, directory).toString();
        }
        currentPath = Paths.get(currentPath, artifactId, version).toString();
        return currentPath;
    }

    private void createPomWithDependencies(List<Dependency> dependencies, String pomPath) throws Exception {
        Model pomModel = new Model();
        pomModel.setGroupId("com.example");
        pomModel.setArtifactId("test-pom");
        pomModel.setPackaging("pom");
        pomModel.setVersion("1.0.0");
        pomModel.setModelVersion("4.0.0");
        pomModel.setDependencies(dependencies);
        File pomFile = new File(pomPath);
        if(pomFile.exists()) {
            pomFile.delete();
        }
        MavenXpp3Writer writer = new MavenXpp3Writer();
        writer.write(new FileWriter(pomFile), pomModel);
        pomFile.deleteOnExit();
    }

    void artifactsInRepoCheck(String repoUrl, List<Artifact> artifacts, boolean doExist) throws Exception {
        for(Artifact artifact : artifacts) {
            String repoPath = getPathFromCoordinates(repoUrl, artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
            File dir =  new File(repoPath);
            //assert(dir.exists() == doExist);
            String name = artifact.getFile().getName();
            String artifactPath = Paths.get(repoPath, name).toString();
            File artifactFile = new File(artifactPath);
            assertTrue(artifactFile.exists() == doExist);
        }
    }

    void removeDir(String dirPath) throws Exception {
        File dir = new File(dirPath);
        FileUtils.deleteDirectory(dir);
        assertTrue(!dir.exists());
    }

    /**
     * Installs stub artifact to local directory. 
     * 
     * @param artifact Artifact to install. 
     * @param mojo Mojo to use for installation. 
     * @return The installed artifact. 
     * @throws Exception
     */
    Artifact installStubArtifact(String groupId, String artifactId, String version, String extension, Optional<String> classifier, MojoBase mojo) throws Exception {
        String tempArtifactsPath = Paths.get(Paths.get("").toString(), "tempArtifacts").toString();
        Artifact artifact = addStubArtifact(groupId, artifactId, version, extension, classifier, tempArtifactsPath);
        return installStubArtifact(artifact, mojo);
    }

    /**
     * Installs a stub pom to the local repository. 
     * 
     * @param groupId Group ID of the pom to install.
     * @param artifactId Artifact ID of the pom to install. 
     * @param version Version of the pom to install. 
     * @param dependencies Dependencies to add to the pom. 
     * @param mojo Mojo used for installation. 
     * @return The install pom artifact. 
     * @throws Exception. 
     */
    Artifact installStubPom(String groupId, String artifactId, String version, List<Dependency> dependencies, MojoBase mojo) throws Exception {
        String pomPath = Paths.get(POM_DIR, String.format("%s-%s.pom", artifactId, version)).toString();
        createPomWithDependencies(dependencies, pomPath);
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "pom", version);
        artifact = artifact.setFile(new File(pomPath));
        return installStubArtifact(artifact, mojo);
    }

    private Artifact installStubArtifact(Artifact artifact, MojoBase mojo) throws Exception {
        InstallRequest artifactRequst = new InstallRequest();
        artifactRequst.addArtifact(artifact);
        InstallResult artifactResult = mojo.repoSystem.install(mojo.mavenSession.getRepositorySession(), artifactRequst);
        Collection<Artifact> installedArtifacts = artifactResult.getArtifacts();
        assert installedArtifacts.size() > 0 : "Could not install artifact";
        Artifact installedArtifact = (Artifact) installedArtifacts.toArray()[0];
        assert installedArtifact.getFile().exists() : "Installed artifact does not exist";
        return installedArtifact;
    }

    /**
     * Adds an artifact with the given coordinates to the url. 
     * 
     * @param groupId Group ID of the artifact. 
     * @param artifactId Artifact ID of the artifact. 
     * @param version Version of the artifact. 
     * @param extension Extension of the artifact. 
     * @param classifier Optional classifier of the artifact. 
     * @param url URL to deploy the artifact to. 
     * @return The installed artifact. 
     * @throws Exception. 
     */
    private Artifact addStubArtifact(String groupId, String artifactId, String version, String extension, Optional<String> classifier, String url) throws Exception {
        createDir(url);
        if(extension.equals("pom")) {
            String newFilePath = Paths.get(url, String.format("%s-%s.pom", artifactId, version)).toString();
            createPomWithDependencies(new ArrayList<>(), newFilePath);
            Artifact artifact = new DefaultArtifact(groupId, artifactId, extension, version);
            return artifact.setFile(new File(newFilePath));
        }
        Artifact artifact;
        String fileName;
        if(classifier.isPresent()) {
            artifact = new DefaultArtifact(groupId, artifactId, classifier.get(), extension, version);
            fileName = String.format("%s-%s-%s.%s", artifactId, version, classifier.get(), extension);
        }
        else {
            artifact = new DefaultArtifact(groupId, artifactId, extension, version);
            fileName = String.format("%s-%s.%s", artifactId, version, extension);
        }
        File testFile = new File(Paths.get(url, fileName).toString());
        if(!testFile.exists()) {
            boolean result = testFile.createNewFile();
            assertTrue(result);
        }
        testFile.deleteOnExit();
        return artifact.setFile(testFile);
    }

    void createDir(String url) {
        File dir = new File(url);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        assertTrue(dir.exists());
        dir.deleteOnExit();
    }

}
