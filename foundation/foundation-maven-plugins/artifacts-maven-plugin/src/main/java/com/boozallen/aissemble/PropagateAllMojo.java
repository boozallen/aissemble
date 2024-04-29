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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Goal used to propagate all artifacts of a project to an alternate repository
 */
@Mojo(name = "propagate-all", requiresDependencyResolution = ResolutionScope.TEST, aggregator = true)
public class PropagateAllMojo extends MojoBase {

    private static final Logger logger = LoggerFactory.getLogger(PropagateMojo.class);

    @Parameter(property = "url", required = true)
    String url;

    @Parameter(property = "repositoryId", required = false, defaultValue = "")
    String repositoryId;
    
    @Override
    public void execute() throws MojoExecutionException {
        ArtifactsGoalHelper goalHelper = new ArtifactsGoalHelper(this);
        goalHelper.deployProjectDependencies(url, repositoryId);
    }

}
