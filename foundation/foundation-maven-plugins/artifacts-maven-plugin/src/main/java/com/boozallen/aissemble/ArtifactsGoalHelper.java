package com.boozallen.aissemble;
/*-
 * #%L
 * artifacts-plugin Maven Mojo
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeploymentException;

import java.util.Set;
import java.util.HashSet;
import org.apache.commons.io.FilenameUtils;

/**
 * Helper class for deploying artifacts to an alternate repository.
 */
public class ArtifactsGoalHelper {

    private static final Logger logger = LoggerFactory.getLogger(ArtifactsGoalHelper.class);
    static String[] extensions = {"pom", "jar", "war"};
    static String[] classifiers = {"sources", "tests", "javadoc"};
    
    private final MojoBase mojo;

    ArtifactsGoalHelper(MojoBase mojo) {
        this.mojo = mojo;
    }

    /**
     * Deploys direct and transitive dependencies from all projects to an alternate repository.  
     * 
     * @param url URL of the alternate repository. 
     * @param repositoryId Credentials used for alternate repository. 
     * @throws MojoExecutionException
     */
    public void deployProjectDependencies(String url, String repositoryId) throws MojoExecutionException {
        checkUrlTransport(url);
        Set<Artifact> allArtifacts = new HashSet<>();
        for(MavenProject project : mojo.mavenSession.getProjects()) {
            Set<Artifact> jarArtifacts = convert(project.getArtifacts());
            for(Artifact jarArtifact : jarArtifacts) {
                allArtifacts.addAll(getArtifactsFromCoordinates(jarArtifact.getGroupId(), jarArtifact.getArtifactId(), jarArtifact.getVersion()));
            }
        }
        deployToAlternateRepo(new ArrayList<>(allArtifacts), repositoryId, url);
    }

    /**
     * Deploy artifacts from given coordinates to an alternate repository. 
     * 
     * @param groupId Group ID of artifact. 
     * @param artifactId Artifact ID of artifact. 
     * @param version Version of artifact. 
     * @param url URL of alternate repository. 
     * @param repositoryId Credentials used for alternate repository. 
     * @throws MojoExecutionException
     */
    public void deployArtifacts(String groupId, String artifactId, String version, String url, String repositoryId) throws MojoExecutionException {
        checkUrlTransport(url);
        Set<Artifact> artifacts = new HashSet<>();
        artifacts.addAll(getArtifactsFromCoordinates(groupId, artifactId, version));
        deployToAlternateRepo(new ArrayList<>(artifacts), repositoryId, url);
    }

    private List<Artifact> getArtifactsFromCoordinates(String groupId, String artifactId, String version) {
        List<Artifact> coordinateArtifacts = new ArrayList<>();
        List<Artifact> potentialArtifacts = getPotentialArtifactsFromCoordinates(groupId, artifactId, version);
        coordinateArtifacts.addAll(getInstalledArtifacts(potentialArtifacts));
        return coordinateArtifacts;
    }

    /**
     * Converts between org.apache.maven.artifact.Artifact and org.eclipse.aether.artifact.Artifact Sets
     * @param artifacts Artifacts to convert. 
     * @return Set of org.eclipse.aether.artifact.Artifact Artifacts. 
     */
    private Set<Artifact> convert(Set<org.apache.maven.artifact.Artifact> artifacts) {
        Set<Artifact> artifactsList = new HashSet<>();
        for(org.apache.maven.artifact.Artifact artifact : artifacts) {
            String groupId = artifact.getGroupId();
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();
            String extension = FilenameUtils.getExtension(artifact.getFile().getName());
            String classifier = artifact.getClassifier();
            Artifact convertedArtifact = new DefaultArtifact(groupId, artifactId, extension, version);
            if(artifact.hasClassifier()) {
                convertedArtifact = new DefaultArtifact(groupId, artifactId, classifier, extension, version);
            }
            artifactsList.add(convertedArtifact);
        }
        return artifactsList;
    }

    private void deployToAlternateRepo(List<Artifact> artifacts, String repositoryId, String url) throws MojoExecutionException {
        RemoteRepository remoteRepository = getRemoteRepository(repositoryId, url);
        DeployRequest deployRequest = new DeployRequest();
        deployRequest.setRepository(remoteRepository);
        deployRequest.setArtifacts(artifacts);
        try {
            mojo.repoSystem.deploy(mojo.mavenSession.getRepositorySession(), deployRequest);
        } catch (DeploymentException e) {
            throw new MojoExecutionException(e);
        }
    }

    private static List<Artifact> getPotentialArtifactsFromCoordinates(String groupId, String artifactId, String version) {
        List<Artifact> artifacts = new ArrayList<>();
        // Checks artifacts of all extensions with no classifiers
        for(String extension : extensions) {
            artifacts.add(new DefaultArtifact(groupId, artifactId, extension, version));
        }

        // Checks all jar artifacts with all extensions
        for(String classifier : classifiers) {
            artifacts.add(new DefaultArtifact(groupId, artifactId, classifier, "jar", version));
        }

        return artifacts;
    }

    private void checkUrlTransport(String url) throws MojoExecutionException {
        if(!(url.contains("file://") || url.contains("http://") || url.contains("https://"))) {
            throw new MojoExecutionException("URL does not have a valid transport method. Must be file://, http://, or https://");
        }
    }

    List<Artifact> getInstalledArtifacts(List<Artifact> artifacts) {
        List<Artifact> installedArtifacts = new ArrayList<>();
        for(Artifact artifact : artifacts) {
            getInstalledArtifact(artifact).ifPresent(installedArtifacts::add);
        }
        return installedArtifacts;
    }

    Optional<Artifact> getInstalledArtifact(Artifact artifact) {
        assert artifact != null : "Internal error: Artifact is null in getInstalledArtifact";
        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(artifact);
        ArtifactResult result;
        try {
            result = mojo.repoSystem.resolveArtifact(mojo.mavenSession.getRepositorySession(), request);
        } catch (ArtifactResolutionException e) {
            // Could not find artifact
            return Optional.empty();
        }
        Artifact artifactResult = result.getArtifact();
        File file = artifactResult.getFile();
        if(file == null || !file.exists()) {
            // Just in case artifact was not found, log and exit
            return Optional.empty();
        }
        logger.info(String.format("Artifact %s exists, queing for deployment", file.getName()));
        return Optional.of(artifactResult);
    }

    private RemoteRepository getRemoteRepository(final String repositoryId, final String url) {
        RemoteRepository result = new RemoteRepository.Builder(repositoryId, "default", url).build();
        if (result.getAuthentication() == null || result.getProxy() == null) {
            RemoteRepository.Builder builder = new RemoteRepository.Builder(result);
            if (result.getAuthentication() == null) {
                builder.setAuthentication(mojo.mavenSession.getRepositorySession()
                        .getAuthenticationSelector()
                        .getAuthentication(result));
            }
            if (result.getProxy() == null) {
                builder.setProxy(
                        mojo.mavenSession.getRepositorySession().getProxySelector().getProxy(result));
            }
            result = builder.build();
        }
        return result;
    }

}
