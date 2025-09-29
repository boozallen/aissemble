package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;

import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

/**
 * This migration updates the pom file to add the habushu plugin for containerization of the spark worker docker image.
 */
public class SparkWorkerContainerizationPomMigration extends AbstractContainerizeMigration {
    public static final String AISSEMBLE_SPARK_WORKER_DOCKER = "aissemble-spark-worker-docker";
    public static final String DOCKER_USER = "spark";

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        MavenProject mavenProject = getMavenProject();
        return isDockerBuildPackage(mavenProject)
                && containsFermenterProfile(mavenProject, AISSEMBLE_SPARK_WORKER_DOCKER)
                && !containsHabushuContainerizeGoal(mavenProject)
                && containerHabushuPackagedDependency(mavenProject);
    }

    @Override
    protected boolean performMigration(File pomFile) {
        detectAndSetIndent(pomFile);
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        PomModifications pomModifications = new PomModifications();

        // Safe to assume the pom has build and plugins because "shouldExecuteOnFile" checks the fermenter plugin
        String dockerBuilder = "root_habushu_builder";
        String dockerFinal = "${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}";
        final String insertHabushuPluginWithContainerizationGoal = habushuPluginWithContainerizationGoal(
                dockerBuilder, dockerFinal, DOCKER_USER, 3);
        pomModifications.add(new PomModifications.Insertion(model.getBuild().getLocation("plugins" + END),
                3, content -> insertHabushuPluginWithContainerizationGoal));

        return PomHelper.writeModifications(pomFile, pomModifications.finalizeMods());
    }
}
