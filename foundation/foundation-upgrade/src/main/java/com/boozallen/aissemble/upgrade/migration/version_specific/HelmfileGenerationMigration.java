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

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;

/**
 * Migration to create the initial helmfile the archetype creates. If projects are upgrading to this version then
 * they will not be running the archetype generation and will not have the default helmfile to add manual actions too
 * . Because projects may choose not to use helmfile, the migration will only run if the helmfile migration enable
 * key is set to true.
 */
public class HelmfileGenerationMigration extends AbstractAissembleMigration {

    private static final Logger logger = LoggerFactory.getLogger(HelmfileGenerationMigration.class);
    private static final String VERSION_TAG_REGEX = "(.*)(\\$\\{archetypeVersion})";
    private static final String HELMFILE_TEMPLATE_PATH = "version_specific/helmfile.yaml";
    private static final String HELMFILE_MIGRATION_ENABLE_KEY = "aissemble.enable.helmfile.migration";

    /**
     * Determines whether the migration should execute.
     * Migration will always run if the helmfile migration enable key is set to true.
     *
     * @param file pom file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {

        String activated = System.getProperty(HELMFILE_MIGRATION_ENABLE_KEY);
        return "true".equalsIgnoreCase(activated);
    }

    /**
     * First checks if helmfile exists. If not, initialize it.
     *
     * @param file pom file to migrate
     * @return true if migration was successful
     */
    @Override
    protected boolean performMigration(File file) {
        createHelmfileIfNotExist();
        return true;
    }

    private void createHelmfileIfNotExist() throws BatonException {
        String helmfileLocation = getRootProject().getBasedir().getPath() + "/helmfile.yaml";
        File file = new File(helmfileLocation);
        if (file.exists()) {
            logger.info("Helmfile already found. Skipping creation and ignoring migration");
        } else {
            try {
                InputStream initialHelmfile = getClass().getClassLoader().getResourceAsStream(HELMFILE_TEMPLATE_PATH);
                if (initialHelmfile == null) {
                    throw new BatonException("Could not find the helmfile template");
                }
                File helmfile = new File(helmfileLocation);
                FileUtils.copyInputStreamToFile(initialHelmfile, helmfile);
                updateArtifactId(helmfile);
            } catch (IOException e) {
                throw new BatonException("Failed to instantiate the helmfile.yaml", e);
            }
            logger.info("Initialized root helmfile.yaml");
        }

    }

    private void updateArtifactId(File file) {
        try (BufferedReader valuesFile = new BufferedReader((new FileReader(file)))) {
            String line;
            Pattern pattern = Pattern.compile(VERSION_TAG_REGEX, Pattern.MULTILINE);

            while ((line = valuesFile.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String substitution = "$1" + getAissembleVersion();
                    replaceInFile(file, VERSION_TAG_REGEX, substitution);
                }
            }
        } catch (IOException e) {
            throw new BatonException("Unable to modify the helmfile's artifact ID.", e);
        }
    }
}
