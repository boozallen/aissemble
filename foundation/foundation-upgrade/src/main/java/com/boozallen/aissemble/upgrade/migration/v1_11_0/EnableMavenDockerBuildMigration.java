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
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import java.io.File;

import org.apache.maven.model.BuildBase;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This migration enables the maven docker build for the pipeline docker modules by removing the fabric8 pluginManagement
 * skip configuration
 */
public class EnableMavenDockerBuildMigration extends AbstractPomMigration {
    private static final String FABRIC8_GROUP_ID = "${group.fabric8.plugin}";
    private static final String FABRIC8_ARTIFACT_ID = "docker-maven-plugin";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        boolean hasSkipConfig = false;
        if (model.getPackaging().equals("pom")) {
            hasSkipConfig = getAllBuilds(model)
                .stream()
                .map(EnableMavenDockerBuildMigration::getDockerPluginIfPresent)
                .map(EnableMavenDockerBuildMigration::getSkipConfig)
                .anyMatch(Objects::nonNull);
        }

        return hasSkipConfig ;
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications modifications;
        modifications = new PomModifications();
        for (BuildBase build : getAllBuilds(model)) {
            Plugin dockerPlugin = getDockerPluginIfPresent(build);
            PomModifications.Deletion deletionMod = getDeleteSkipConfigModification(dockerPlugin);
            if (deletionMod != null) {
                modifications.add(deletionMod);
            }
        }
        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    private ArrayList<BuildBase> getAllBuilds(Model model) {
        ArrayList<BuildBase> builds = new ArrayList<>();
        if( model.getBuild() != null ) {
            builds.add(model.getBuild());
        }
        model.getProfiles().stream()
                .map(Profile::getBuild)
                .forEach(builds::add);
        return builds;
    }

    private static Plugin getDockerPluginIfPresent(BuildBase build) {
        Plugin dockerPlugin = null;
        if (build != null && build.getPluginManagement() != null && build.getPluginManagement().getPlugins() != null) {
            for (Plugin plugin : build.getPluginManagement().getPlugins()) {
                if (FABRIC8_GROUP_ID.equals(plugin.getGroupId()) && FABRIC8_ARTIFACT_ID.equals(plugin.getArtifactId())) {
                    dockerPlugin = plugin;
                    break;
                }
            }
        }
        return dockerPlugin;
    }

    private PomModifications.Deletion getDeleteSkipConfigModification(Plugin plugin) {
        Xpp3Dom skip = getSkipConfig(plugin);
        if (skip != null) {
            String value = skip.getValue();
            InputLocation skipInputLocation = (InputLocation) skip.getInputLocation();
            InputLocation start = PomHelper.incrementColumn(skipInputLocation, - "<skip>".length());
            InputLocation end = PomHelper.incrementColumn(skipInputLocation, value.length() + "</skip>".length());
            return new PomModifications.Deletion(start, end);
        }
        return null;
    }

    private static Xpp3Dom getSkipConfig(Plugin plugin) {
        if (plugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
            if (configuration != null) {
                return configuration.getChild("skip");
            }
        }
        return null;
    }
}
