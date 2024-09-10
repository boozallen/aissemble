package com.boozallen.aissemble.upgrade.migration.v1_8_0;

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
import com.boozallen.aissemble.upgrade.util.AissembleFileUtils;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Profile;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.technologybrewery.baton.util.pom.PomModifications;
import org.technologybrewery.baton.util.pom.PomModifications.Deletion;
import org.technologybrewery.baton.util.pom.PomModifications.Insertion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.boozallen.aissemble.upgrade.migration.v1_8_0.OrphedomosToFabric8Migration.FABRIC8_ARTIFACT_ID;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.END;
import static org.technologybrewery.baton.util.pom.LocationAwareMavenReader.START;

/**
 * In the move from Orphedomos to Fabric8, we can no longer specify the Docker plugin as part of the build for a module
 * that is not a `docker-build` packaging type.  (Orphedomos allowed this and simply skipped all goals if the packaging
 * type was not `orphedomos`.)  This migration ensures any `pom` type modules only specify the Docker plugin within a
 * pluginManagement section.
 */
public class Fabric8LocationMigration extends AbstractPomMigration {
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = PomHelper.getLocationAnnotatedModel(file);
        String packaging = model.getPackaging();
        if (POM.equals(packaging)) {
            boolean profilesMatch = model.getProfiles()
                    .stream()
                    .map(Profile::getBuild)
                    .map(Fabric8LocationMigration::getDockerPluginIfPresent)
                    .anyMatch(Objects::nonNull);
            return profilesMatch || getDockerPluginIfPresent(model.getBuild()) != null;
        } else {
            return false;
        }
    }

    @Override
    protected boolean performMigration(File file) {
        detectAndSetIndent(file);
        Model model = PomHelper.getLocationAnnotatedModel(file);
        PomModifications modifications;
        modifications = new PomModifications();
        for (BuildBase build : getAllBuilds(model)) {
            Plugin dockerPlugin = getDockerPluginIfPresent(build);
            if (dockerPlugin != null) {
                movePlugin(modifications, dockerPlugin, build, file);
            }
        }
        return PomHelper.writeModifications(file, modifications.finalizeMods());
    }

    private void movePlugin(PomModifications modifications, Plugin dockerPlugin, BuildBase build, File file) {
        List<String> content = getPluginContent(dockerPlugin, file);
        boolean removePluginsSection = removePluginFromPlugins(modifications, dockerPlugin, build);
        addPluginToManagement(modifications, build, removePluginsSection, content);
    }

    private void addPluginToManagement(PomModifications modifications, BuildBase build, boolean removePluginsSection, List<String> content) {
        InputLocation insertLocation;
        PluginManagement management = build.getPluginManagement();
        if (management == null ) {
            InputLocation pluginsStart = build.getLocation("plugins" + START);
            insertLocation = removePluginsSection ? build.getLocation(END) : pluginsStart;
            wrapPluginWithManagementTags(pluginsStart.getColumnNumber(), content);
        } else {
            insertLocation = management.getLocation("plugins" + END);
        }
        String concat = String.join("\n", content) + "\n";
        modifications.add(new Insertion(insertLocation, 1, ignore -> concat));
    }

    private List<String> getPluginContent(Plugin dockerPlugin, File file) {
        //NB: InputLocations are 1-based
        int copyStart = dockerPlugin.getLocation(START).getLineNumber() - 1;
        int copyEnd = dockerPlugin.getLocation(END).getLineNumber() - 1;
        return AissembleFileUtils.getLines(file.toPath(), copyStart, copyEnd)
                .stream()
                .map(line -> indent + line) // because we're moving from plugins to pluginManagement/plugins
                .collect(Collectors.toList());
    }

    private boolean removePluginFromPlugins(PomModifications modifications, Plugin dockerPlugin, BuildBase build) {
        InputLocation deleteStart = dockerPlugin.getLocation(START);
        InputLocation deleteEnd = dockerPlugin.getLocation(END);
        boolean removePluginsSection = build.getPlugins().size() == 1;
        if (removePluginsSection) {
            deleteStart = build.getLocation("plugins" + START);
            deleteEnd = build.getLocation("plugins" + END);
        }
        modifications.add(new Deletion(deleteStart, deleteEnd));
        return removePluginsSection;
    }

    private void wrapPluginWithManagementTags(int startColumn, List<String> content) {
        int level = startColumn /indent.length();
        content.add(0, repeat(indent, level) + "<pluginManagement>");
        content.add(1, repeat(indent, level + 1) + "<plugins>");
        content.add(repeat(indent, level + 1) + "</plugins>");
        content.add(repeat(indent, level) + "</pluginManagement>");
    }

    private static ArrayList<BuildBase> getAllBuilds(Model model) {
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
        if (build != null && build.getPlugins() != null) {
            for (Plugin plugin : build.getPlugins()) {
                if (FABRIC8_ARTIFACT_ID.equals(plugin.getArtifactId())) {
                    dockerPlugin = plugin;
                    break;
                }
            }
        }
        return dockerPlugin;
    }
}
