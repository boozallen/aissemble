package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.MigrationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.boozallen.aissemble.upgrade.util.FileUtils.getRegExCaptureGroups;

public class HelmChartsV1Migration extends AbstractAissembleMigration {
    public static final Logger logger = LoggerFactory.getLogger(HelmChartsV1Migration.class);
    private static final String versionReplaceRegex = "(tag: *)\\\"?\\d+\\.\\d+\\.\\d+(?:[a-zA-Z-\\d+]*)\\\"?";;
    public static final String extractVersionRegex = "tag: *\"?(\\d+\\.\\d+\\.\\d+[-a-zA-Z\\.\\d+]*)\"?";

    private String mavenAissembleVersion;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;

        if (file != null && file.exists()) {
            try {
                // get current app version from pom for comparison
                mavenAissembleVersion = getAissembleVersion();

                if (mavenAissembleVersion != null) {
                    ArrayList<String> chartAissembleVersions = getRegExCaptureGroups(extractVersionRegex, file);
                    if (!chartAissembleVersions.isEmpty())
                        shouldExecute = chartAissembleVersions.stream().anyMatch(chartVersion ->
                                MigrationUtils.isLessThanVersion(chartVersion, mavenAissembleVersion)
                        );
                } else {
                    logger.error("Unable to parse version from current project");
                }
            } catch (IOException e) {
                logger.error("Unable to load file into yaml class due to exception:", e);
            }
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;

        try {
            String replacementText = FIRST_REGEX_GROUPING.concat(QUOTE + mavenAissembleVersion + QUOTE);
            performedSuccessfully = FileUtils.replaceInFile(
                    file,
                    versionReplaceRegex,
                    replacementText
            );

        } catch (Exception e) {
            logger.error("Unable to perform v1 helm charts migration due to exception", e);
        }

        return performedSuccessfully;
    }
}
