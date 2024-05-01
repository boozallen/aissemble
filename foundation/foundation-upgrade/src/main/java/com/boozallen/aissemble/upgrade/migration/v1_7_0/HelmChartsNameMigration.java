package com.boozallen.aissemble.upgrade.migration.v1_7_0;

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
import com.boozallen.aissemble.upgrade.util.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import static com.boozallen.aissemble.upgrade.util.FileUtils.getRegExCaptureGroups;

/**
 * Baton migration used to migrate the Chart.yaml and values*.yaml files within a project's deploy folder
 * to use the new Helm chart naming convention (aissemble-<chart-name>-chart)
 */

public class HelmChartsNameMigration extends AbstractAissembleMigration {
    public static final Logger logger = LoggerFactory.getLogger(HelmChartsNameMigration.class);
    private static final String dependencyNameReplaceRegex = "(aissemble-[^\\/\\n:]+)(?<!-chart)$";
    private static final String appNameReplaceRegex = "(^\\s*)(aissemble-[^\\/\\n:]+)(?<!-chart):$";
    private static final String COLON = ":";
    private static final String SECOND_REGEX_GROUPING = "$2";
    private static final String CHART_POSTFIX = "-chart";

    /**
     * Function to check whether migration is necessary for the given file.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try {
            if (file.getName().matches("Chart.yaml")){
                List<String> dependencyNames = getRegExCaptureGroups(dependencyNameReplaceRegex, file);
                shouldExecute = CollectionUtils.isNotEmpty(dependencyNames);
            } else {
                List<String> appNames = getRegExCaptureGroups(appNameReplaceRegex, file);
                shouldExecute = CollectionUtils.isNotEmpty(appNames);
            }
        } catch (IOException e) {
            throw new BatonException("Unable to determine if Helm charts should be migrated!", e);
        }

        if (shouldExecute) {
            logger.info("Found Helm chart file using old naming convention. Migrating file: {}", file.getName());
        }
        return shouldExecute;
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true.
     * @param file file to migrate
     * @return performedSuccessfully - Whether the file was migrated successfully.
     */
    @Override
    protected boolean performMigration(File file) {
        boolean performedSuccessfully = false;

        try {

            String appNameReplacementText = FIRST_REGEX_GROUPING.concat(SECOND_REGEX_GROUPING + CHART_POSTFIX + COLON);
            String dependencyNameReplacementText = FIRST_REGEX_GROUPING.concat(CHART_POSTFIX);

            if (file.getName().matches("Chart.yaml")){
                performedSuccessfully = FileUtils.replaceInFile(
                        file,
                        dependencyNameReplaceRegex,
                        dependencyNameReplacementText
                );
            } else {
                performedSuccessfully = FileUtils.replaceInFile(
                        file,
                        appNameReplaceRegex,
                        appNameReplacementText
                );
            }
        } catch (Exception e) {
            logger.error("Unable to perform helm chart name migration due to exception", e);
        }

        return performedSuccessfully;
    }

}
