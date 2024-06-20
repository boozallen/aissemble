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
import com.boozallen.aissemble.upgrade.util.pom.LocationAwareMavenReader;
import org.apache.maven.model.Build;
import org.technologybrewery.baton.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.pom.PomHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.boozallen.aissemble.upgrade.util.pom.PomModifications;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import static com.boozallen.aissemble.upgrade.util.pom.PomHelper.getLocationAnnotatedModel;
import static com.boozallen.aissemble.upgrade.util.pom.PomHelper.writeModifications;

import static org.technologybrewery.baton.util.FileUtils.getRegExCaptureGroups;

/**
 * This migration is responsible for migrating pom files using Orphedomos to use the Fabric8 docker-maven-plugin.
 */

public class OrphedomosToFabric8Migration extends AbstractAissembleMigration{
    private static final Logger logger = LoggerFactory.getLogger(OrphedomosToFabric8Migration.class);
    private static final String ORPHEDOMOS_GROUP_ID = "org.technologybrewery.orphedomos";
    private static final String ORPHEDOMOS_ARTIFACT_ID = "orphedomos-maven-plugin";
    private static final String FABRIC8_GROUP_ID = "io.fabric8";
    private static final String FABRIC8_ARTIFACT_ID = "docker-maven-plugin";
    private static final String EXTRACT_PACKAGING_REGEX = "(<packaging>)orphedomos(<\\/packaging>)";
    private static final String SECOND_REGEX_GROUPING = "$2";
    private static final String ROOT_ARTIFACT_ID = "build-parent";
    private static final String ROOT_GROUP_ID = "com.boozallen.aissemble";
    private static final String TESTS_DOCKER_PARENT_ARTIFACT_ID = "-tests";
    private static final String TESTS_DOCKER_ARTIFACT_ID = "-tests-docker";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String CONFIGURATION = "configuration";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_VERSION = "imageVersion";
    private static String imageName;
    private static String imageVersion;

    /**
     * Function that returns instance of Plugin class with name equal to the pluginName provided
     * @param model the model to check
     * @param pluginName the name of the plugin
     * @return Plugin with the plugin name specified
     */
    private Plugin getPlugin(Model model, String pluginName) {
        Build build = model.getBuild();
        if (build != null) {
            List<Plugin> plugins = build.getPlugins();
            for (Plugin plugin : plugins) {
                if ((pluginName).equalsIgnoreCase(plugin.getArtifactId())) {
                    return plugin;
                }
            }
        }
        return null;
    }

    private boolean isRootProjectPom(Model model) {
        return ROOT_GROUP_ID.equals(model.getParent().getGroupId()) && ROOT_ARTIFACT_ID.equals(model.getParent().getArtifactId());
    }
    private boolean isTestDockerPom(Model model){
        return model.getParent().getArtifactId().endsWith(TESTS_DOCKER_PARENT_ARTIFACT_ID) && model.getArtifactId().endsWith(TESTS_DOCKER_ARTIFACT_ID);
    }

    private Xpp3Dom getConfiguration(Plugin plugin) {
        Object pluginConfiguration = plugin.getConfiguration();
        return (Xpp3Dom) pluginConfiguration;
    }

    private static boolean hasConfigurationItem(Xpp3Dom configuration, String configurationItem){
        return configuration.getChildren(configurationItem).length > 0;
    }

    /**
     * Function to validate whether migration should be performed or not.
     * @param file file to validate
     * @return true if migration should be performed
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        boolean migrateConfiguration = false;
        boolean migratePackaging = false;

        Model model = getLocationAnnotatedModel(file);
        if (isRootProjectPom(model) || isTestDockerPom(model)){
            shouldExecute = false;
        } else {
            Plugin orphedomosPlugin = getPlugin(model, ORPHEDOMOS_ARTIFACT_ID);
            if (orphedomosPlugin != null){
                migrateConfiguration = true;
            }
            if(containsOrphedomosPackaging(file)){
                migratePackaging = true;
            }
            shouldExecute = migrateConfiguration || migratePackaging;
        }

        return shouldExecute;
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true
     * @param file file to migrate
     * @return isSuccessful - whether file was migrated successfully or not
     */
    @Override
    protected boolean performMigration(File file) {
        Model model = getLocationAnnotatedModel(file);
        Plugin plugin = getPlugin(model, ORPHEDOMOS_ARTIFACT_ID);
        boolean replacedConfig = false;
        boolean replacedPackaging = false;
        boolean isSuccessful = false;

        Xpp3Dom config;

        if (plugin != null){
            config = getConfiguration(plugin);
        } else {
            config = null;
            logger.info("Orphedomos plugin configuration not found. Skipping configuration migration.");
        }

        // check if pom file has orphedomos-maven-plugin configuration
        if (config != null){
            // grab the old imageVersion and imageName
            imageVersion = getImageVersion(config);
            imageName = getImageName(config);

            // Replace groupID and artifactID
            PomModifications modifyPlugin = new PomModifications();
            InputLocation startGroupId = plugin.getLocation(GROUP_ID);
            InputLocation endGroupId = PomHelper.incrementColumn(startGroupId, ORPHEDOMOS_GROUP_ID.length());

            InputLocation startArtifactId = plugin.getLocation(ARTIFACT_ID);
            InputLocation endArtifactId = PomHelper.incrementColumn(startArtifactId, ORPHEDOMOS_ARTIFACT_ID.length());

            modifyPlugin.add(new PomModifications.Replacement(startGroupId, endGroupId, FABRIC8_GROUP_ID));
            modifyPlugin.add(new PomModifications.Replacement(startArtifactId, endArtifactId, FABRIC8_ARTIFACT_ID));

            writeModifications(file, modifyPlugin.finalizeMods());

            Model updatedModel = getLocationAnnotatedModel(file);
            Plugin updatedPlugin = getPlugin(updatedModel, FABRIC8_ARTIFACT_ID);

            if (imageVersion != null && imageName != null) {
                PomModifications modifyImages = new PomModifications();
                // add new configuration code
                int indent = 4;
                int startConfigCodeRow = updatedPlugin.getLocation(ARTIFACT_ID).getLineNumber();
                InputLocation startConfigCode = new InputLocation(startConfigCodeRow + 1, updatedPlugin.getLocation(ARTIFACT_ID).getColumnNumber());
                modifyImages.add(new PomModifications.Insertion(startConfigCode, indent, OrphedomosToFabric8Migration::getConfigCode));

                replacedConfig = writeModifications(file, modifyImages.finalizeMods());

            }

            Model fabric8Model = getLocationAnnotatedModel(file);
            Plugin fabric8Plugin = getPlugin(fabric8Model, FABRIC8_ARTIFACT_ID);
            PomModifications removeOldConfig = new PomModifications();

            // delete single line version tag if it exists
            String version = fabric8Plugin.getVersion();
            if (version != null) {
                InputLocation startVersionLocation = fabric8Plugin.getLocation(VERSION + LocationAwareMavenReader.START);
                InputLocation endVersionLocation = fabric8Plugin.getLocation(VERSION + LocationAwareMavenReader.END);

                PomModifications.Deletion pomDeletePreviousVersion = new PomModifications.Deletion(startVersionLocation, endVersionLocation);
                removeOldConfig.add(pomDeletePreviousVersion);
            }

            // delete old orphedomos configuration
            InputLocation startConfigLocation = fabric8Plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.START);
            InputLocation endConfigLocation = fabric8Plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.END);
            PomModifications.Deletion pomDeletePreviousConfig = new PomModifications.Deletion(startConfigLocation, endConfigLocation);
            removeOldConfig.add(pomDeletePreviousConfig);

            writeModifications(file, removeOldConfig.finalizeMods());
        }

        // check if pom file has <packaging>orphedomos</packaging> and replace
        if (containsOrphedomosPackaging(file)){
            replacedPackaging = executePackagingMigration(file);
        }

        isSuccessful = replacedConfig || replacedPackaging;

        return isSuccessful;
    }


    private String getImageName(Xpp3Dom configuration){
        String imageName;
        if (hasConfigurationItem(configuration, IMAGE_NAME)) {
            imageName = configuration.getChild(IMAGE_NAME).getValue();
        } else {
            imageName = null;
        }
        return imageName;
    }

    private String getImageVersion(Xpp3Dom configuration){
        String imageVersion;
        if (hasConfigurationItem(configuration, IMAGE_VERSION)) {
            imageVersion = configuration.getChild(IMAGE_VERSION).getValue();
        } else {
            imageVersion = null;
        }
        return imageVersion;
    }

    /**
     * Function that creates the new Fabric8 docker-maven-plugin configuration code.
     */
    private static String getConfigCode(String indent){
        return StringUtils.repeat(indent, 4) + "<executions>\n" +
                StringUtils.repeat(indent, 5) + "<execution>\n" +
                StringUtils.repeat(indent, 6) + "<id>default-build</id>\n" +
                StringUtils.repeat(indent, 6) + "<configuration>\n" +
                StringUtils.repeat(indent, 7) + "<images>\n" +
                StringUtils.repeat(indent, 8) + "<image>\n" +
                StringUtils.repeat(indent, 9) + "<name>" + imageName + ":" + imageVersion + "</name>\n" +
                StringUtils.repeat(indent, 8) + "</image>\n" +
                StringUtils.repeat(indent, 7) + "</images>\n" +
                StringUtils.repeat(indent, 6) + "</configuration>\n" +
                StringUtils.repeat(indent, 5) + "</execution>\n" +
                StringUtils.repeat(indent, 4) + "</executions>\n";
    }

    /**
     * Function to check whether a given pom file has its packaging type set to Orphedomos
     */
    private boolean containsOrphedomosPackaging(File file){
        boolean shouldExecute = false;
        try {
            List<String> orphedomosPackaging = getRegExCaptureGroups(EXTRACT_PACKAGING_REGEX, file);
            shouldExecute = CollectionUtils.isNotEmpty(orphedomosPackaging);
        } catch (IOException e){
            throw new BatonException("Unable to determine if Orphedomos packaging migration must be executed", e);
        }
        return shouldExecute;
    }

    /**
     * Function that performs the migration on the pom file's packaging type.
     */
    private boolean executePackagingMigration(File file){
        boolean performedSuccessfully = false;
        String replacementText = FIRST_REGEX_GROUPING + "docker-build" + SECOND_REGEX_GROUPING;
        try {
            performedSuccessfully = FileUtils.replaceInFile(
                    file,
                    EXTRACT_PACKAGING_REGEX,
                    replacementText
            );
        } catch (Exception e) {
            logger.error("Unable to perform packaging type migration due to exception", e);
        }

        return performedSuccessfully;
    }

}
