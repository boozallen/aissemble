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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.Component;
import org.eclipse.aether.RepositorySystem;

/**
 * Base class for Mojos
 */
public abstract class MojoBase extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(required = true, readonly = true, defaultValue = "${session}")
    MavenSession mavenSession;

    @Component
    RepositorySystem repoSystem;

    @Component
    BuildPluginManager pluginManager;

}
