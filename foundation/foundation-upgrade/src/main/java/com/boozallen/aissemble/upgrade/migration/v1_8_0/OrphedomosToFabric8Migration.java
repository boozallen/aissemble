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
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.InputLocation;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Profile;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.technologybrewery.baton.util.pom.PomModifications;
import org.technologybrewery.baton.util.pom.PomModifications.Modification;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import static org.technologybrewery.baton.util.pom.PomHelper.getLocationAnnotatedModel;
import static org.technologybrewery.baton.util.pom.PomHelper.writeModifications;

import static org.technologybrewery.baton.util.FileUtils.getRegExCaptureGroups;

/**
 * This migration is responsible for migrating pom files using Orphedomos to use the Fabric8 docker-maven-plugin.
 */

public class OrphedomosToFabric8Migration extends AbstractAissembleMigration {
    public static final String FABRIC8_GROUP_ID = "${group.fabric8.plugin}";
    public static final String FABRIC8_ARTIFACT_ID = "docker-maven-plugin";
    public static final String FABRIC8_PACKAGING = "docker-build";
    private static final String ORPHEDOMOS_GROUP_ID = "org.technologybrewery.orphedomos";
    private static final String ORPHEDOMOS_ARTIFACT_ID = "orphedomos-maven-plugin";
    private static final String ORPHEDOMOS_PACKAGING = "orphedomos";
    private static final String SPACE = " ";
    private static final Logger logger = LoggerFactory.getLogger(OrphedomosToFabric8Migration.class);
    private static final String EXTRACT_PACKAGING_REGEX = "(<packaging>)orphedomos(<\\/packaging>)";
    private static final String PACKAGING = "packaging";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String IMAGE_NAME = "imageName";
    private static final String IMAGE_VERSION = "imageVersion";
    private static final String REPO_URL = "repoUrl";
    private static final String CONFIGURATION = "configuration";
    private static final String CI = "ci";
    private static final String INTEGRATION_TEST = "integration-test";

    private String buildImageName;
    private String buildImageVersion;
    private String buildRepoUrl;

    /**
     * Function to validate whether migration should be performed or not.
     * @param file file to validate
     * @return true if migration should be performed
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        Model model = getLocationAnnotatedModel(file);

        Boolean hasOrphedomosBuildPluginsCount = !getAllBuildPluginsByGroupIdAndArtifactId(model, ORPHEDOMOS_GROUP_ID, ORPHEDOMOS_ARTIFACT_ID).isEmpty();
        Boolean hasOrphedomosProfilePluginsCount = !getAllProfilePluginsByGroupIdAndArtifactId(model, ORPHEDOMOS_GROUP_ID, ORPHEDOMOS_ARTIFACT_ID).isEmpty();

        if (hasOrphedomosBuildPluginsCount|| hasOrphedomosProfilePluginsCount || containsOrphedomosPackaging(file)) {
            logger.info("Orphedomos plugin configuration found. Migrating the following file: {}", file.getAbsolutePath());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true
     * @param file file to migrate
     * @return isSuccessful - whether file was migrated successfully or not
     */
    @Override
    protected boolean performMigration(File file) {
        Model model = getLocationAnnotatedModel(file);
        PomModifications modifications = new PomModifications();

        List<Plugin> buildPlugins = getAllBuildPluginsByGroupIdAndArtifactId(model, ORPHEDOMOS_GROUP_ID, ORPHEDOMOS_ARTIFACT_ID);
        if (!buildPlugins.isEmpty()) {
            for (Plugin buildPlugin: buildPlugins) {
                    modifications.addAll(getAllPluginModifications(file, buildPlugin, null));
            }
        }

        //check if pom file has <packaging>orphedomos</packaging> and replace
        if (containsOrphedomosPackaging(file)) {
            modifications.add(getPackagingMigrationMod(model));
        }

        List<Pair<String, Plugin>> profilePluginPairs = getAllProfilePluginsByGroupIdAndArtifactId(model, ORPHEDOMOS_GROUP_ID, ORPHEDOMOS_ARTIFACT_ID);
        if (!profilePluginPairs.isEmpty()) {
            for (Pair<String, Plugin> profilePluginPair: profilePluginPairs) {
                modifications.addAll(getAllPluginModifications(file, profilePluginPair.getRight(), profilePluginPair.getLeft()));
            }
        }

        return writeModifications(file, modifications.finalizeMods());
    }

    private Predicate<Plugin> getFilterByGroupAndArtifactId(String pluginGroupId, String pluginArtifactId) {
        return plugin -> plugin.getGroupId().equals(pluginGroupId) 
                       && plugin.getArtifactId().equals(pluginArtifactId);
    }

    /**
     * Returns alls instances of {@link Plugin} with the matching group id and artifact id in the build section of a given pom model
     * @param model      the model to check
     * @param pluginGroupId the group id of the plugin
     * @param pluginArtifactId the artifact id of the plugin
     * @return {@link List} of {@link Plugin}s
     */
    private List<Plugin> getAllBuildPluginsByGroupIdAndArtifactId(Model model, String pluginGroupId, String pluginArtifactId) {
        List<Plugin> plugins = new ArrayList<Plugin>();
        Predicate<Plugin> pluginGroupArtifactIdFilter = getFilterByGroupAndArtifactId(pluginGroupId, pluginArtifactId);

        // Check the main build
        Build build = model.getBuild();
        if (build != null) {
            // Get the build plugins
            plugins.addAll(build.getPlugins().stream()
                .filter(pluginGroupArtifactIdFilter)
                .collect(Collectors.toCollection(ArrayList::new))
            );

            // Get the build plugin management
            if (build.getPluginManagement() != null) {
                plugins.addAll(build.getPluginManagement().getPlugins().stream()
                    .filter(pluginGroupArtifactIdFilter)
                    .collect(Collectors.toCollection(ArrayList::new))
                );
            }
        }

        return plugins;
    }

    /**
     * Returns alls instances of {@link Plugin} with the matching group id and artifact id in the profile section of a given pom model
     * @param model      the model to check
     * @param pluginGroupId the group id of the plugin
     * @param pluginArtifactId the artifact id of the plugin
     * @return {@link List} of {@link Pair}s of the {@link String} profile name paired with its respective {@link Plugin}
     */
    private List<Pair<String, Plugin>> getAllProfilePluginsByGroupIdAndArtifactId(Model model, String pluginGroupId, String pluginArtifactId) {
        List<Pair<String, Plugin>> profilePluginsPairs = new ArrayList<>();
        Predicate<Plugin> pluginGroupArtifactIdFilter = getFilterByGroupAndArtifactId(pluginGroupId, pluginArtifactId);

        // Check the build within each profile
        List<Profile> profiles = model.getProfiles();
        for (Profile profile: profiles) {

            BuildBase profileBuild = profile.getBuild();

            if (profileBuild != null) {
                List<Plugin> matchingPlugins = new ArrayList<>();

                // Check the profile build plugins
                matchingPlugins.addAll(profileBuild.getPlugins().stream()
                    .filter(pluginGroupArtifactIdFilter)
                    .collect(Collectors.toCollection(ArrayList::new))
                );

                // Check the profile build plugin management
                if (profileBuild.getPluginManagement() != null) {
                    matchingPlugins.addAll(profileBuild.getPluginManagement().getPlugins().stream()
                        .filter(pluginGroupArtifactIdFilter)
                        .collect(Collectors.toCollection(ArrayList::new))
                    );
                }

                // If there are matching plugins, add them to the list of pairs
                if (!matchingPlugins.isEmpty()) {
                    matchingPlugins.stream().forEach(plugin -> profilePluginsPairs.add(Pair.of(profile.getId(), plugin)));
                }
            }
        }
        
        return profilePluginsPairs;
    }

    /**
     * Returns all {@link Modification}s necessary for migrating an Orphedomos {@link Plugin} instance to fabric8
     * @param file
     * @param plugin
     * @param profileName
     * @return
     */
    private List<Modification> getAllPluginModifications(File file, Plugin plugin, String profileName) {
        List<Modification> modifications = new ArrayList<>();

        // Replace groupID and artifactID
        modifications.addAll(getReplaceGroupIdArtifactIdMod(plugin));

        // check if pom file has orphedomos configuration
        Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();

        if (config != null) {
            // grab the old imageVersion and imageName and repoUrl
            String imageVersion = getConfigValue(config, IMAGE_VERSION);
            String imageName = getConfigValue(config, IMAGE_NAME);
            String repoUrl = getConfigValue(config, REPO_URL);
    
            // check if it's a profile or build plugin instance
            if (profileName == null) {
                if (imageName == null) {
                    imageName = "${project.artifactId}";
                }
                if (imageVersion == null) {
                    imageVersion = "${project.version}";
                }

                // set the values from the build plugin so they can be used as defaults in the profile config
                buildRepoUrl = repoUrl;
                buildImageName = imageName;
                buildImageVersion = imageVersion;

            } else {
                // Use values from build plugin config as defaults if available
                if (imageName == null) {
                    imageName = buildImageName != null ? buildImageName : "${project.artifactId}";
                }
                if (imageVersion == null) {
                    imageVersion = buildImageVersion != null ? buildImageVersion : "${project.version}";
                }
                if (repoUrl == null && !profileName.equals(INTEGRATION_TEST)) {
                    repoUrl = buildRepoUrl != null ? buildRepoUrl : "${docker.project.repository.url}";
                }
            }

            // Replace orphedomos configuration
            modifications.add(getReplaceOrphedomosConfigMod(plugin, repoUrl, imageName, imageVersion, profileName));
        }

        // delete single line version tag if it exists
        if (plugin.getVersion() != null) {
            modifications.add(getDeleteSingleLineVersionTagMod(plugin));
        }

        return modifications;
    }

    private List<PomModifications.Replacement> getReplaceGroupIdArtifactIdMod(Plugin plugin) {
        InputLocation startGroupId = plugin.getLocation(GROUP_ID);
        InputLocation endGroupId = PomHelper.incrementColumn(startGroupId, ORPHEDOMOS_GROUP_ID.length());

        InputLocation startArtifactId = plugin.getLocation(ARTIFACT_ID);
        InputLocation endArtifactId = PomHelper.incrementColumn(startArtifactId, ORPHEDOMOS_ARTIFACT_ID.length());

        List<PomModifications.Replacement> pomReplacements = new ArrayList<>();
        pomReplacements.add(new PomModifications.Replacement(startGroupId, endGroupId, FABRIC8_GROUP_ID));
        pomReplacements.add(new PomModifications.Replacement(startArtifactId, endArtifactId, FABRIC8_ARTIFACT_ID));
        return pomReplacements;
    }

    private PomModifications.Replacement getPackagingMigrationMod(Model model) {
        InputLocation startPackaging = model.getLocation(PACKAGING);
        InputLocation endPackaging = PomHelper.incrementColumn(startPackaging, ORPHEDOMOS_PACKAGING.length());

        return new PomModifications.Replacement(startPackaging, endPackaging, FABRIC8_PACKAGING);
    }

    private PomModifications.Deletion getDeleteSingleLineVersionTagMod(Plugin plugin) {
        InputLocation startVersionLocation = plugin.getLocation(VERSION + LocationAwareMavenReader.START);
        InputLocation endVersionLocation = plugin.getLocation(VERSION + LocationAwareMavenReader.END);

        return new PomModifications.Deletion(startVersionLocation, endVersionLocation);
    }

    private PomModifications.Replacement getReplaceOrphedomosConfigMod(Plugin plugin, String repoUrl, String imageName, String imageVersion, String profileName) {
        InputLocation startConfigLocation = plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.START);
        InputLocation endConfigLocation = plugin.getLocation(CONFIGURATION + LocationAwareMavenReader.END);

        return new PomModifications.Replacement(startConfigLocation, endConfigLocation, 1, getPluginConfig(repoUrl, imageName, imageVersion, profileName));
    }

    /**
     * Function that creates the new Fabric8 docker-maven-plugin configuration code.
     */
    private static UnaryOperator<String> getPluginConfig(String repoUrl, String imageName, String imageVersion, String profileName) {
        // set the specific config for all build plugins and these two profiles
        if (profileName == null || profileName.equals(INTEGRATION_TEST) || profileName.equals(CI)) {
            Boolean shouldSkip = profileName == null ? true : false; // profiles should to set skip to false
            return  (indent) ->
                    indent + "<configuration>\n" +
                    indent + StringUtils.repeat(SPACE, 4) + "<skip>" + shouldSkip.toString() + "</skip>\n" +
                    indent + StringUtils.repeat(SPACE, 4) + "<images>\n" +
                    indent + StringUtils.repeat(SPACE, 4 * 2) + "<image>\n" +
                    indent + StringUtils.repeat(SPACE, 4 * 3) + "<name>" + getNameConfig(repoUrl, imageName, imageVersion, profileName) + "</name>\n" +
                    indent + StringUtils.repeat(SPACE, 4 * 2) + "</image>\n" +
                    indent + StringUtils.repeat(SPACE, 4) + "</images>\n" +
                    indent + "</configuration>\n";
        } 
        // set the config to blank for all other profiles
        else {
            return  (indent) ->
                    indent + "<configuration>\n" +
                    indent + StringUtils.repeat(SPACE, 4) + "<!-- Orphedomos configuration overwritten. Update with respective fabric8 config.-->\n" +
                    indent + "</configuration>\n";
        }
    }

    private static String getNameConfig(String repoUrl, String imageName, String imageVersion, String profileName) {
        if (repoUrl != null && !repoUrl.isBlank()) {
            return repoUrl + imageName + ":" + imageVersion;
        } else {
            return imageName + ":" + imageVersion;
        }
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
}