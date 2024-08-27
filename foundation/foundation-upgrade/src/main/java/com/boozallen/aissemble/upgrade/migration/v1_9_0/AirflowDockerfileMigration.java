package com.boozallen.aissemble.upgrade.migration.v1_9_0;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.technologybrewery.baton.util.FileUtils.replaceLiteralInFile;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;


/**
 * Baton migration class to identify whether Airflow's Dockerfile needs migration or not. If it does, then it will migrate
 * the Dockerfile to reference the community Airflow docker image.
 **/

public class AirflowDockerfileMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(AirflowDockerfileMigration.class);

    public static final String FROM_BOOZALLEN_AISSEMBLE_AIRFLOW_DOCKERFILE = "FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-airflow:1.7.0";
    public static final String FROM_APACHE_AIRFLOW_DOCKER_STRING = "FROM apache/airflow:2.9.3";

    /**
     * Function to check whether the migration is necessary.
     *
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        try (BufferedReader dockerfileReferenceConfig = new BufferedReader((new FileReader(file)))) {
            String line;
            while ((line = dockerfileReferenceConfig.readLine()) != null) {
                if (line.contains(FROM_BOOZALLEN_AISSEMBLE_AIRFLOW_DOCKERFILE)) {
                    shouldExecute = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether Airflow Dockerfile needs migration.");
        }
        return shouldExecute;
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true.
     *
     * @param file file to migrate
     * @return isMigrated - Whether the file was migrated successfully.
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated = false;

        try {
            // update FROM to point to community docker image:
            replaceLiteralInFile(file, FROM_BOOZALLEN_AISSEMBLE_AIRFLOW_DOCKERFILE, FROM_APACHE_AIRFLOW_DOCKER_STRING);

            // remove ARG that are no longer needed:
            List<String> removalTargets = new ArrayList<>();
            List<String> lines = readAllFileLines(file);
            for (String line : lines) {
                if ("ARG DOCKER_BASELINE_REPO_ID".equals(line) || "ARG VERSION_AISSEMBLE".equals(line)) {
                    removalTargets.add(line);
                }
            }
            lines.removeAll(removalTargets);
            writeFile(file, lines);

            isMigrated = true;

        } catch (Exception e) {
            logger.error("Error in performing the migration for a refactored java package.");
        }
        return isMigrated;
    }
}