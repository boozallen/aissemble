package com.boozallen.aissemble.upgrade.migration.v1_10_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

/**
 * Updates the Maven Shade Plugin with the new Log4j dependency information
 */
public class Log4jMavenShadePluginMigration extends AbstractAissembleMigration {
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String MAVEN_SHADE_PLUGIN_GROUP_ID = "org.apache.maven.plugins";
    private static final String MAVEN_SHADE_PLUGIN_ARTIFACT_ID = "maven-shade-plugin";
    private static final String OLD_TRANSFORMER_IMPLEMENTATION = "implementation=\"io.github.edwgiz.log4j.maven.plugins.shade.transformer.Log4j2PluginCacheFileTransformer\"";
    private static final String NEW_TRANSFORMER_IMPLEMENTATION = "implementation=\"org.apache.logging.log4j.maven.plugins.shade.transformer.Log4j2PluginCacheFileTransformer\"";
    private static final String OLD_DEPENDENCY_GROUP_ID = "io.github.edwgiz";
    private static final String NEW_DEPENDENCY_GROUP_ID = "org.apache.logging.log4j";
    private static final String OLD_DEPENDENCY_ARTIFACT_ID = "log4j-maven-shade-plugin-extensions";
    private static final String NEW_DEPENDENCY_ARTIFACT_ID = "log4j-transform-maven-shade-plugin-extensions";
    private static final String OLD_DEPENDENCY_VERSION = "${version.log4j}";
    private static final String NEW_DEPENDENCY_VERSION = "${version.log4j.transform}";
    

    @Override
    protected boolean shouldExecuteOnFile(File file) { 
        Model model = PomHelper.getLocationAnnotatedModel(file);
        if (model.getBuild() != null) {
            return this.getMavenShadePluginOldDependency(model.getBuild()).isPresent();
        } else {
            return false;
        }
        
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications modifications = new PomModifications();

        // Update the dependency with the new group and artifact id
        Dependency mavenShadeDependency = this.getMavenShadePluginOldDependency(model.getBuild()).get();

        InputLocation startGroupId = mavenShadeDependency.getLocation(GROUP_ID);
        InputLocation endGroupId = PomHelper.incrementColumn(startGroupId, OLD_DEPENDENCY_GROUP_ID.length());

        InputLocation startArtifactId = mavenShadeDependency.getLocation(ARTIFACT_ID);
        InputLocation endArtifactId = PomHelper.incrementColumn(startArtifactId, OLD_DEPENDENCY_ARTIFACT_ID.length());

        InputLocation startVersion = mavenShadeDependency.getLocation(VERSION);
        InputLocation endVersion = PomHelper.incrementColumn(startVersion, OLD_DEPENDENCY_VERSION.length());

        modifications.add(new PomModifications.Replacement(startGroupId, endGroupId, NEW_DEPENDENCY_GROUP_ID));
        modifications.add(new PomModifications.Replacement(startArtifactId, endArtifactId, NEW_DEPENDENCY_ARTIFACT_ID));
        modifications.add(new PomModifications.Replacement(startVersion, endVersion, NEW_DEPENDENCY_VERSION));

        // Update the transformer implementation
        try {
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                 .replace(OLD_TRANSFORMER_IMPLEMENTATION, NEW_TRANSFORMER_IMPLEMENTATION));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update the transformer configuration with the new dependency implementation", e);
        }

        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    /**
     * Returns the old log4j dependency within the maven shade plugin
     */
    private Optional<Dependency> getMavenShadePluginOldDependency(Build build) {
        return build.getPlugins()
                    .stream()
                    .filter(plugin -> 
                        plugin.getGroupId().equals(MAVEN_SHADE_PLUGIN_GROUP_ID) && 
                        plugin.getArtifactId().equals(MAVEN_SHADE_PLUGIN_ARTIFACT_ID)
                    )
                    .findFirst()
                    .flatMap(plugin -> 
                        plugin.getDependencies()
                              .stream()
                              .filter(dependency -> 
                                    dependency.getGroupId().equals(OLD_DEPENDENCY_GROUP_ID) && 
                                    dependency.getArtifactId().equals(OLD_DEPENDENCY_ARTIFACT_ID) &&
                                    dependency.getVersion().equals(OLD_DEPENDENCY_VERSION)
                              )
                              .findFirst()
                    );
    }               
}
