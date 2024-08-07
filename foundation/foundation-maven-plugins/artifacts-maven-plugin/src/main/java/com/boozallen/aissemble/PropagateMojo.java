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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal used to propagate a single coordinate's artifact to an alternate repository
 */
@Mojo(name = "propagate")
public class PropagateMojo extends MojoBase {
    @Parameter(property = "groupId", required = true)
    String groupId;

    @Parameter(property = "artifactId", required = true)
    String artifactId;

    @Parameter(property = "version", required = true)
    String version;

    @Parameter(property = "url", required = true)
    String url;

    @Parameter(property = "repositoryId", required = false, defaultValue = "")
    String repositoryId;

    @Override
    public void execute() throws MojoExecutionException {
        ArtifactsGoalHelper artifactsGoalHelper = new ArtifactsGoalHelper(this);
        artifactsGoalHelper.deployArtifacts(groupId, artifactId, version, url, repositoryId);
    }

}
