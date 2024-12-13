package com.boozallen.aissemble.upgrade.migration.v1_11_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractPomMigration;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This migration fixes the build cache dependency check by removing the dependency `pom` type for the java pipeline
 * and replace the dependency type with `habushu` if it's python pipeline
 */
public class DockerModulePomDependencyTypeMigration extends AbstractPomMigration {
    private static final String PIPELINE_GROUP_ID = "${project.groupId}";
    private static final String JAR = "jar";
    private static final String HABUSHU = "habushu";
    private static final String POM = "pom";

    @Override
    protected boolean shouldExecuteOnFile(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);
        if (model.getPackaging().equals("docker-build")) {
            // find the pipeline artifacts
            Map<String, String> pipelineArtifactIds = getPipelineArtifactIds();
            return getDependenciesNeedToBeUpdated(model, pipelineArtifactIds).size() > 0;
        }
        return false;
    }

    @Override
    protected boolean performMigration(File pomFile) {
        Model model = PomHelper.getLocationAnnotatedModel(pomFile);

        PomModifications modifications;
        Map<String, String> pipelineArtifactIds = getPipelineArtifactIds();
        List<Dependency> dependenciesToBeUpdated = getDependenciesNeedToBeUpdated(model, pipelineArtifactIds);

        modifications = new PomModifications();
        for (Dependency dependency: dependenciesToBeUpdated) {

            if (POM.equals(dependency.getType())) {
                if (pipelineArtifactIds.get(dependency.getArtifactId()).equals(JAR)) {
                    // delete the dependency type if it's java pipeline
                    modifications.add(deleteTag(dependency, "type"));
                } else {
                    // replace the dependency type if it's python pipeline
                    modifications.add(replaceInTag(dependency, "type", HABUSHU));
                }
            }
        }
        return PomHelper.writeModifications(pomFile, modifications.finalizeMods());
    }

    private Map<String, String> getPipelineArtifactIds() {
        Map<String, String> pipelines= new HashMap<>();
        for (MavenProject project : getRootProject().getCollectedProjects()) {
            String packaging = project.getPackaging();
            if( project.getParent().getArtifactId().endsWith("-pipelines") && !packaging.equals("pom")) {
                pipelines.put(project.getArtifactId(), packaging);
            }
        }
        return pipelines;
    }

    private List<Dependency> getDependenciesNeedToBeUpdated(Model model, Map<String, String> currentPipelines) {
         return this.getMatchingDependenciesForProject(model, dependency -> {
            String dependencyType = currentPipelines.get(dependency.getArtifactId());
            if (dependencyType != null && dependency.getGroupId().equals(PIPELINE_GROUP_ID)) {
                return !dependencyType.equals(dependency.getType());
            }
            return false;
        });
    }
}
