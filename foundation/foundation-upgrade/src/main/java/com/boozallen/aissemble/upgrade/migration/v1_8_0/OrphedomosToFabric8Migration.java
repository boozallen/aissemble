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
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.technologybrewery.baton.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.pom.PomHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class OrphedomosToFabric8Migration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(OrphedomosToFabric8Migration.class);
    private static final String ORPHEDOMOS_GROUP_ID = "org.technologybrewery.orphedomos";
    private static final String ORPHEDOMOS_ARTIFACT_ID = "orphedomos-maven-plugin";
    private static final String FABRIC8_GROUP_ID = "io.fabric8";
    private static final String FABRIC8_ARTIFACT_ID = "docker-maven-plugin";
    private static final String EXTRACT_PACKAGING_REGEX = "(<packaging>)orphedomos(<\\/packaging>)";
    private static final String SECOND_REGEX_GROUPING = "$2";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_VERSION = "imageVersion";
    private static final String REPO_URL = "repoUrl";
    private static final String CONFIGURATION = "configuration";
    private static final String CI = "ci";
    private static final String INTEGRATION_TEST = "integration-test";
    private static String imageName;
    private static String imageVersion;
    private static String repoUrl;
    private static String profile;

    /**
     * Function to validate whether migration should be performed or not.
     * @param file file to validate
     * @return true if migration should be performed
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = getLocationAnnotatedModel(file);

        Plugin orphedomosBuildPlugin = getBuildPlugin(model, ORPHEDOMOS_ARTIFACT_ID);

        Plugin orphedomosCiProfilePlugin = getProfilePlugin(model, ORPHEDOMOS_ARTIFACT_ID, CI);
        Plugin orphedomosItProfilePlugin = getProfilePlugin(model, ORPHEDOMOS_ARTIFACT_ID, INTEGRATION_TEST);

        return orphedomosBuildPlugin != null
                || orphedomosCiProfilePlugin != null
                || orphedomosItProfilePlugin != null
                || containsOrphedomosPackaging(file);
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true
     * @param file file to migrate
     * @return isSuccessful - whether file was migrated successfully or not
     */
    @Override
    protected boolean performMigration(File file) {
        Model model = getLocationAnnotatedModel(file);
        Plugin buildPlugin = getBuildPlugin(model, ORPHEDOMOS_ARTIFACT_ID);
        boolean replacedBuildConfig = false;
        boolean replacedCiProfile = false;
        boolean replacedItProfile = false;
        boolean replacedPackaging = false;

        Xpp3Dom config;

        if (buildPlugin != null) {
            config = getConfiguration(buildPlugin);
        } else {
            config = null;
            logger.info("Orphedomos plugin configuration not found. Skipping configuration migration.");
        }

        // check if pom file has orphedomos-maven-plugin configuration
        if (config != null) {
            replacedBuildConfig = executeBuildConfigMigration(file, config, buildPlugin);
        }

        // check if pom file has <packaging>orphedomos</packaging> and replace
        if (containsOrphedomosPackaging(file)) {
            replacedPackaging = executePackagingMigration(file);
        }

        model = getLocationAnnotatedModel(file);
        Plugin ciProfilePlugin = getProfilePlugin(model, ORPHEDOMOS_ARTIFACT_ID, CI);
        Plugin itProfilePlugin = getProfilePlugin(model, ORPHEDOMOS_ARTIFACT_ID, INTEGRATION_TEST);

        // check if pom file has orphedomos plugin in profile
        if (ciProfilePlugin != null) {
            profile = CI;
            replacedCiProfile = executeProfileMigration(file, ciProfilePlugin, CI);
        }
        if (itProfilePlugin != null) {
            profile = INTEGRATION_TEST;
            replacedItProfile = executeProfileMigration(file, itProfilePlugin, INTEGRATION_TEST);
        }

        return replacedBuildConfig || replacedCiProfile || replacedItProfile || replacedPackaging;
    }

    /**
     * Returns instance of Plugin class with name equal to the pluginName provided, in the pom's build block
     * @param model      the model to check
     * @param pluginName the name of the plugin
     * @return Plugin with the plugin name specified
     */
    private Plugin getBuildPlugin(Model model, String pluginName) {
        Build build = model.getBuild();
        if (build != null) {
            List<Plugin> plugins = build.getPlugins();
            for (Plugin plugin : plugins) {
                if (pluginName.equalsIgnoreCase(plugin.getArtifactId())) {
                    return plugin;
                }
            }
        }
        return null;
    }

    /**
     * Returns instance of Plugin class with name equal to the pluginName provided, in the pom's profiles block
     * @param model      the model to check
     * @param pluginName the name of the plugin
     * @param profileId  the profile housing the plugin
     * @return Plugin with the plugin name specified
     */
    private Plugin getProfilePlugin(Model model, String pluginName, String profileId) {
        List<Profile> profiles = model.getProfiles();
        if (profiles != null) {
            List<Plugin> plugins = profiles.stream()
                    .filter(profile -> profile.getId().equals(profileId))
                    .flatMap(profile -> profile.getBuild().getPlugins().stream())
                    .collect(Collectors.toCollection(ArrayList::new));
            for (Plugin plugin : plugins) {
                if (pluginName.equalsIgnoreCase(plugin.getArtifactId())) {
                    return plugin;
                }
            }
        }
        return null;
    }

    private Xpp3Dom getConfiguration(Plugin plugin) {
        Object pluginConfiguration = plugin.getConfiguration();
        return (Xpp3Dom) pluginConfiguration;
    }

    private String getConfigValue(Xpp3Dom configuration, String parameter) {
        String parameterValue;
        if (hasConfigurationItem(configuration, parameter)) {
            parameterValue = configuration.getChild(parameter).getValue();
        } else {
            parameterValue = null;
        }
        return parameterValue;
    }

    private static boolean hasConfigurationItem(Xpp3Dom configuration, String configurationItem) {
        return configuration.getChildren(configurationItem).length > 0;
    }

    /**
     * Function to check whether a given pom file has its packaging type set to Orphedomos
     */
    private boolean containsOrphedomosPackaging(File file) {
        boolean shouldExecute;
        try {
            List<String> orphedomosPackaging = getRegExCaptureGroups(EXTRACT_PACKAGING_REGEX, file);
            shouldExecute = CollectionUtils.isNotEmpty(orphedomosPackaging);
        } catch (IOException e) {
            throw new BatonException("Unable to determine if Orphedomos packaging migration must be executed", e);
        }
        return shouldExecute;
    }

    private boolean executeBuildConfigMigration(File file, Xpp3Dom config, Plugin buildPlugin) {
        // grab the old imageVersion and imageName
        imageVersion = getConfigValue(config, IMAGE_VERSION);
        imageName = getConfigValue(config, IMAGE_NAME);

        if (imageName == null) {
            imageName = "${project.artifactId}";
        }
        if (imageVersion == null) {
            imageVersion = "${project.version}";
        }

        // Replace groupID and artifactID
        PomModifications modifyPlugin = getReplaceGroupIdArtifactIdMod(buildPlugin);
        boolean replacedGroupIdArtifactId = writeModifications(file, modifyPlugin.finalizeMods());

        // get the latest plugin after previous modification
        Plugin fabric8Plugin = getCurrentBuildPlugin(file);

        // delete old orphedomos configuration
        PomModifications removeOldConfig = new PomModifications();
        if (fabric8Plugin != null) {
            updateModWithDeleteOrphedomosConfig(fabric8Plugin, removeOldConfig);

            // delete single line version tag if it exists
            if (fabric8Plugin.getVersion() != null) {
                updateModWithDeleteSingleLineVersionTag(fabric8Plugin, removeOldConfig);
            }
        }

        boolean replacedConfig = writeModifications(file, removeOldConfig.finalizeMods());

        // get the latest plugin after previous modification
        Plugin updatedBuildPlugin = getCurrentBuildPlugin(file);

        // add the plugin image configuration
        boolean replacedImageConfig = false;
        PomModifications modifyConfig = getImageConfigAddMod(updatedBuildPlugin);
        replacedImageConfig = writeModifications(file, modifyConfig.finalizeMods());

        return replacedGroupIdArtifactId || replacedImageConfig || replacedConfig;
    }

    private Plugin getCurrentBuildPlugin(File file) {
        Model updatedModel = getLocationAnnotatedModel(file);
        return getBuildPlugin(updatedModel, FABRIC8_ARTIFACT_ID);
    }

    private Plugin getCurrentProfilePlugin(File file, String profileName) {
        Model updatedModel = getLocationAnnotatedModel(file);
        return getProfilePlugin(updatedModel, FABRIC8_ARTIFACT_ID, profileName);
    }

    private PomModifications getReplaceGroupIdArtifactIdMod(Plugin plugin) {
        PomModifications modifyPlugin = new PomModifications();
        InputLocation startGroupId = plugin.getLocation(GROUP_ID);
        InputLocation endGroupId = PomHelper.incrementColumn(startGroupId, ORPHEDOMOS_GROUP_ID.length());

        InputLocation startArtifactId = plugin.getLocation(ARTIFACT_ID);
        InputLocation endArtifactId = PomHelper.incrementColumn(startArtifactId, ORPHEDOMOS_ARTIFACT_ID.length());

        modifyPlugin.add(new PomModifications.Replacement(startGroupId, endGroupId, FABRIC8_GROUP_ID));
        modifyPlugin.add(new PomModifications.Replacement(startArtifactId, endArtifactId, FABRIC8_ARTIFACT_ID));
        return modifyPlugin;
    }

    private PomModifications getImageConfigAddMod(Plugin updatedBuildPlugin) {
        PomModifications modifyImages = new PomModifications();

        // add new configuration code
        int indent = 4;
        int startConfigCodeRow = updatedBuildPlugin.getLocation(ARTIFACT_ID).getLineNumber();
        InputLocation startConfigCode = new InputLocation(startConfigCodeRow + 1, updatedBuildPlugin.getLocation(ARTIFACT_ID).getColumnNumber());
        modifyImages.add(new PomModifications.Insertion(startConfigCode, indent, OrphedomosToFabric8Migration::getBuildConfigCode));

        return modifyImages;
    }

    /**
     * Function that creates the new Fabric8 docker-maven-plugin configuration code.
     */
    private static String getBuildConfigCode(String indent) {
        return StringUtils.repeat(indent, 5) + "<configuration>\n" +
                StringUtils.repeat(indent, 6) + "<skip>true</skip>\n" +
                StringUtils.repeat(indent, 6) + "<images>\n" +
                StringUtils.repeat(indent, 7) + "<image>\n" +
                StringUtils.repeat(indent, 8) + "<name>" + imageName + ":" + imageVersion + "</name>\n" +
                StringUtils.repeat(indent, 7) + "</image>\n" +
                StringUtils.repeat(indent, 6) + "</images>\n" +
                StringUtils.repeat(indent, 5) + "</configuration>\n";
    }

    private void updateModWithDeleteOrphedomosConfig(Plugin fabric8Plugin, PomModifications removeOldConfig) {
        InputLocation startConfigLocation = fabric8Plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.START);
        InputLocation endConfigLocation = fabric8Plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.END);
        PomModifications.Deletion pomDeletePreviousConfig = new PomModifications.Deletion(startConfigLocation, endConfigLocation);
        removeOldConfig.add(pomDeletePreviousConfig);
    }

    private void updateModWithDeleteSingleLineVersionTag(Plugin fabric8Plugin, PomModifications removeOldConfig) {
        InputLocation startVersionLocation = fabric8Plugin.getLocation(VERSION + LocationAwareMavenReader.START);
        InputLocation endVersionLocation = fabric8Plugin.getLocation(VERSION + LocationAwareMavenReader.END);

        PomModifications.Deletion pomDeletePreviousVersion = new PomModifications.Deletion(startVersionLocation, endVersionLocation);
        removeOldConfig.add(pomDeletePreviousVersion);
    }

    private boolean executeProfileMigration(File file, Plugin profilePlugin, String profileId) {
        Xpp3Dom config = getConfiguration(profilePlugin);
        repoUrl = getConfigValue(config, REPO_URL);
        imageVersion = getConfigValue(config, IMAGE_VERSION);
        imageName = getConfigValue(config, IMAGE_NAME);

        if (repoUrl == null) {
            repoUrl = "${docker.project.repository.url}";
        }
        if (imageName == null) {
            imageName = "${project.artifactId}";
        }
        if (imageVersion == null) {
            imageVersion = "${project.version}";
        }

        // Replace groupID and artifactID in the profile's plugin
        PomModifications modifyPlugin = getReplaceGroupIdArtifactIdMod(profilePlugin);
        boolean replacedGroupIdArtifactId = writeModifications(file, modifyPlugin.finalizeMods());

        // get the latest plugin after previous modification
        Plugin fabric8Plugin = getCurrentProfilePlugin(file, profileId);

        // delete old orphedomos configuration
        PomModifications removeOldConfig = new PomModifications();
        if (fabric8Plugin != null) {
            updateModWithDeleteOrphedomosConfig(fabric8Plugin, removeOldConfig);

            // delete single line version tag if it exists
            if (fabric8Plugin.getVersion() != null) {
                updateModWithDeleteSingleLineVersionTag(fabric8Plugin, removeOldConfig);
            }
        }
        boolean replacedConfig = writeModifications(file, removeOldConfig.finalizeMods());

        // get the latest plugin after previous modification
        Plugin updatedProfilePlugin = getCurrentProfilePlugin(file, profileId);

        // add the relevant profile's configuration accordingly
        boolean replacedProfilePluginConfig;
        PomModifications modifyPluginConfig = getProfilePluginConfigMod(updatedProfilePlugin);
        replacedProfilePluginConfig = writeModifications(file, modifyPluginConfig.finalizeMods());

        return replacedGroupIdArtifactId || replacedProfilePluginConfig || replacedConfig;
    }

    private PomModifications getProfilePluginConfigMod(Plugin updatedProfilePlugin) {
        PomModifications modifyProfile = new PomModifications();
        int indent = 4;
        int startConfigCodeRow = updatedProfilePlugin.getLocation(ARTIFACT_ID).getLineNumber();
        InputLocation startConfigCode = new InputLocation(startConfigCodeRow + 1, updatedProfilePlugin.getLocation(ARTIFACT_ID).getColumnNumber());
        modifyProfile.add(new PomModifications.Insertion(startConfigCode, indent, OrphedomosToFabric8Migration::getProfilePluginConfig));
        return modifyProfile;
    }

    private static String getProfilePluginConfig(String indent) {
        return StringUtils.repeat(indent, 5) + "<configuration>\n" +
                StringUtils.repeat(indent, 6) + "<skip>false</skip>\n" +
                StringUtils.repeat(indent, 6) + "<images>\n" +
                StringUtils.repeat(indent, 7) + "<image>\n" +
                getNameConfig(indent) +
                StringUtils.repeat(indent, 7) + "</image>\n" +
                StringUtils.repeat(indent, 6) + "</images>\n" +
                StringUtils.repeat(indent, 5) + "</configuration>\n";
    }

    private static String getNameConfig(String indent) {
        if (profile.equals(INTEGRATION_TEST)) {
            return StringUtils.repeat(indent, 8) + "<name>" + imageName + ":" + imageVersion + "</name>\n";
        } else {
            return StringUtils.repeat(indent, 8) + "<name>" + repoUrl + "/" + imageName + ":" + imageVersion + "</name>\n";
        }
    }

    /**
     * Function that performs the migration on the pom file's packaging type.
     */
    private boolean executePackagingMigration(File file) {
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
