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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import org.technologybrewery.baton.util.pom.LocationAwareMavenReader;
import org.technologybrewery.baton.util.pom.PomModifications;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import java.io.File;
import java.util.List;

import static org.technologybrewery.baton.util.pom.PomHelper.getLocationAnnotatedModel;
import static org.technologybrewery.baton.util.pom.PomHelper.writeModifications;

/**
 * This migration disables failing on Python linting for existing projects.
 */
public class PythonLintingMigration extends AbstractAissembleMigration {
    private Plugin getHabushuPlugin(Model model) {
        List<Plugin> plugins = model.getBuild().getPluginManagement().getPlugins();
        for (Plugin plugin : plugins) {
            if ("habushu-maven-plugin".equalsIgnoreCase(plugin.getArtifactId())) {
                return plugin;
            }
        }
        return null;
    }

    private Xpp3Dom getConfiguration(Plugin plugin) {
        Object lintingConfiguration = plugin.getConfiguration();
        return (Xpp3Dom) lintingConfiguration;
    }

    private boolean hasLinting(Xpp3Dom configuration) {
        if (configuration.getChildren("sourceFailOnLintErrors").length > 0 || configuration.getChildren("testFailOnLintErrors").length > 0) {
            return true;
        }
        return false;
    }

    private boolean isRootProjectPom(Model model) {
        return "com.boozallen.aissemble".equals(model.getParent().getGroupId()) && "build-parent".equals(model.getParent().getArtifactId());
    }

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = getLocationAnnotatedModel(file);
        if (!isRootProjectPom(model)) {
            return false;
        }
        
        Plugin habushuPlugin = getHabushuPlugin(model);

        if(habushuPlugin == null) {
            return true;
        }

        Xpp3Dom configuration = getConfiguration(habushuPlugin);
        
        if (configuration == null) {
            return true;
        }
        
        if (!hasLinting(configuration)) {
            return !hasLinting(configuration);
        }
        return false;
    }

    private static String getLintingCode(String indent) {
        return StringUtils.repeat(indent, 6) +  "<sourceFailOnLintErrors>false</sourceFailOnLintErrors>\n" + 
            StringUtils.repeat(indent, 6) +  "<testFailOnLintErrors>false</testFailOnLintErrors>\n";
    }

    private static String getConfigurationCode(String indent) {
        return StringUtils.repeat(indent, 5) + "<configuration>\n" + 
            getLintingCode(indent) +
            StringUtils.repeat(indent, 5) + "</configuration>\n";
    }

    private static String getHabushuPluginCode(String indent) {
        return StringUtils.repeat(indent, 4) + "<plugin>\n" +
            StringUtils.repeat(indent, 5) + "<groupId>org.technologybrewery.habushu</groupId>\n" + 
            StringUtils.repeat(indent, 5) + "<artifactId>habushu-maven-plugin</artifactId>\n" + 
            getConfigurationCode(indent) +
            StringUtils.repeat(indent, 4) + "</plugin>\n";
    }

    @Override
    protected boolean performMigration(File file) {
        Model model = getLocationAnnotatedModel(file);
        Plugin habushuPlugin = getHabushuPlugin(model);
        Xpp3Dom configuration;
        if (habushuPlugin != null) {
            configuration = getConfiguration(habushuPlugin);
        } else {
            configuration = null;
        }
        int indent;
        InputLocation inputLocation;
        String indentStr;
        PomModifications.Insertion pomInsertion;
        
        if (habushuPlugin == null) {
            InputLocation referenceLocation = model.getBuild().getPluginManagement().getLocation("plugins" + LocationAwareMavenReader.END);
            inputLocation = new InputLocation(referenceLocation.getLineNumber(), referenceLocation.getColumnNumber());
            indent = 3;
            pomInsertion = new PomModifications.Insertion(inputLocation, indent, PythonLintingMigration::getHabushuPluginCode);
        } else if (configuration == null) {
            InputLocation referenceLocation = habushuPlugin.getLocation(LocationAwareMavenReader.END);
            inputLocation = new InputLocation(referenceLocation.getLineNumber(), referenceLocation.getColumnNumber());
            indent = 4;
            pomInsertion = new PomModifications.Insertion(inputLocation, indent, PythonLintingMigration::getConfigurationCode);
        } else {
            InputLocation referenceLocation = habushuPlugin.getLocation("configuration" + LocationAwareMavenReader.END);
            inputLocation = new InputLocation(referenceLocation.getLineNumber(), referenceLocation.getColumnNumber());
            indent = 5;
            pomInsertion = new PomModifications.Insertion(inputLocation, indent, PythonLintingMigration::getLintingCode);
        }
        PomModifications modifications = new PomModifications();
        modifications.add(pomInsertion);
        return writeModifications(file, modifications.finalizeMods());
    }
}