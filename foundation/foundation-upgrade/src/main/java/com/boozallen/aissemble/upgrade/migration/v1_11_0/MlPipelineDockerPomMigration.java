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
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;

/**
 * This migration updates machine learning pipeline docker POM to include step artifact dependency for resolving the build cache dependency check issue.
 */
public class MlPipelineDockerPomMigration extends AbstractPomMigration {

    private static final String PROJECT_GROUP_ID = "${project.groupId}";
    private static final String STEP_PACKAGE_OUTPUT_DIR = "^<outputDirectory>\\$\\{project\\.build\\.directory}/(.+)<\\/outputDirectory>$";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        if (model.getPackaging().equals("docker-build")) {
            String stepArtifactId = getMlPipelineStepArtifact(file);
            return stepArtifactId != null && !model.getDependencies().stream()
                    .anyMatch(dependency -> isMlStep(dependency, stepArtifactId));
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        String stepArtifactId = getMlPipelineStepArtifact(file);
        detectAndSetIndent(file);
        PomModifications modifications = new PomModifications();
        // check if there is dependencies
        if (model.getDependencies().size() > 0) {
            String content = getDependency(stepArtifactId);
            modifications.add(
                    new PomModifications.Insertion(model.getLocation("dependencies" + END), 2, ignore -> content));
        } else {
            String content = getDependencies(stepArtifactId);
            modifications.add(
                    new PomModifications.Insertion(model.getLocation(END), 1, ignore -> content));
        }

        PomHelper.writeModifications(file, modifications.finalizeMods());
        return true;
    }

    private static boolean isMlStep(Dependency dependency, String expectedArtifact) {
        return dependency != null &&
                dependency.getGroupId().equals(PROJECT_GROUP_ID) &&
                dependency.getArtifactId().equals(expectedArtifact);
    }

    private String getMlPipelineStepArtifact(File file) {
        try {
            String step = null;
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line: lines) {
                line = line.trim();
                if (line.matches(STEP_PACKAGE_OUTPUT_DIR)) {
                    step = line.replaceAll(STEP_PACKAGE_OUTPUT_DIR, "$1");
                    break;
                }
            }
            if (getStepArtifactIds().contains(step)) {
                return step;
            }
        } catch (IOException e) {
            throw new BatonException("Could not get Ml pipeline step artifactId at: " + file.getPath(), e);
        }
        return null;
    }

    private String getDependencies(String stepArtifactId) {
        return repeat(indent, 1) + "<dependencies>\n" +
                getDependency(stepArtifactId) +
                repeat(indent, 1) + "</dependencies>\n";
    }

    private String getDependency(String stepArtifactId) {
        return repeat(indent, 2) + "<dependency>\n" +
                repeat(indent, 3) + "<groupId>" + PROJECT_GROUP_ID + "</groupId>\n" +
                repeat(indent, 3) + "<artifactId>" + stepArtifactId + "</artifactId>\n" +
                repeat(indent, 3) + "<version>${project.version}</version>\n" +
                repeat(indent, 3) + "<type>habushu</type>\n" +
                repeat(indent, 2) + "</dependency>\n" ;
    }

    protected List<String> getStepArtifactIds() {
        List<String> stepArtifacts= new ArrayList<>();
        for (MavenProject project : getRootProject().getCollectedProjects()) {
            Plugin plugin = project.getPlugin(Plugin.constructKey("org.technologybrewery.fermenter", "fermenter-mda"));
            String pluginConfigProfile = getConfigProfileValue(plugin);
            if( pluginConfigProfile != null && pluginConfigProfile.equals("machine-learning-pipeline")) {
                for (String module: project.getModules()) {
                    stepArtifacts.add(module.replaceAll("<\\/?module>", ""));
                }
                break;
            }
        }
        return stepArtifacts;
    }

    private static String getConfigProfileValue(Plugin plugin) {
        if (plugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
            if (configuration != null) {
                if (configuration.getChild("profile") != null) {
                    return configuration.getChild("profile").getValue();
                };
            }
        }
        return null;
    }
}
