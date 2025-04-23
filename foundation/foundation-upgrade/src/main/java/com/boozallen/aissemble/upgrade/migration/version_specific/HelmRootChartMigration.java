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

import org.apache.commons.io.FileUtils;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;

/**
 * Migration to move files from *-deploy/ (root deploy directory) to the common-infrastructure folder
 */
public class HelmRootChartMigration extends AbstractHelmfileMigration {
    private static final String CHART_NAME_REGEX = "(.*)(name: )(.*)";
    private static final String CHART_NAME_REPLACEMENT_REGEX = "name: common-infrastructure";
    private static final String CHART_YAML = "Chart.yaml";

    /**
     * Migration will move the file to the apps/common-infrastructure folder
     * @param file helm charts
     * @return true if migration was successfully performed
     */
    @Override
    protected boolean performMigration(File file) {
        try {
            // move the yaml file to the apps/common-infrastructure folder
            Path targetPath = Paths.get(file.getParentFile().getCanonicalPath(),
                    "apps", "common-infrastructure");

            FileUtils.moveFileToDirectory(file, targetPath.toFile(), true);

            // After migrating the Chart.yaml we need to change the name property to common-infrastructure
            if (file.getName().equalsIgnoreCase(CHART_YAML)){
                Path migratedChartFile = Paths.get(targetPath.toString(), "Chart.yaml");
                changeChartNameProperty(migratedChartFile.toFile());
            }
        } catch (IOException e) {
            throw new BatonException("Could not migrate file: " + file.getPath(), e);
        }

        return true;
    }

    /**
     * Determines whether the migration should execute.
     * if the helmfile migration enable key is set and file is an ArgoCD application template, the migration will run.
     *
     * @param file yaml file to check
     * @return true if the migration should run
     */
    @Override
    protected boolean shouldExecuteOnFileImpl(File file) {
        try {
            // In our case we want to migrate files that are not argocd
            return !isArgoCDApplicationTemplate(file);
        } catch (Exception e) {
            throw new BatonException("Could not check yaml file for ArgoCD removal: " + file.getPath(), e);
        }
    }

    protected boolean changeChartNameProperty(File file) {
        boolean isMigrated;

        try {
            String substitution = "$1" + CHART_NAME_REPLACEMENT_REGEX;
            isMigrated = replaceInFile(file, CHART_NAME_REGEX, substitution);
        } catch (IOException e) {
            throw new BatonException("Could not change the name property of Chart.yaml: " + file.getPath(), e);
        }

        return isMigrated;
    }
}
