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
import com.boozallen.aissemble.upgrade.pojo.Chart;
import com.boozallen.aissemble.upgrade.pojo.MlflowValues;
import com.boozallen.aissemble.upgrade.pojo.MlflowValues.Mlflow;
import com.boozallen.aissemble.upgrade.pojo.Chart.Dependency;
import com.boozallen.aissemble.upgrade.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.YamlUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class MlflowV2ExternalS3Migration extends AbstractAissembleMigration {
    
    private static final Logger logger = LoggerFactory.getLogger(MlflowV2ExternalS3Migration.class);
    private static final String MLFLOW_V2_CHART_NAME = "aissemble-mlflow";
    private static final String SPACE = " ";
    private File valuesFile = null;
    private MlflowValues valuesObject = null;
    private File valuesDevFile = null;
    private MlflowValues valuesDevObject = null;

    /**
     * Function to find an Mlflow v2 chart deployment and its respective values files. Path to the chart cannot be safely assumed
     * so we're checking the dependencies of each Chart.yaml within the project for the aissemble-mlflow dependency.
     */
    @Override
    protected boolean shouldExecuteOnFile(File chartFile) {
        if (isMlflowV2Chart(chartFile)) {
            // find the respective values.yaml and values-dev.yaml
            this.valuesFile = findFileInDirectory("values.yaml", chartFile.getParentFile().getAbsoluteFile());
            this.valuesDevFile = findFileInDirectory("values-dev.yaml", chartFile.getParentFile().getAbsoluteFile());

            // Check files for external s3 config if both were found
            if (this.valuesFile != null && this.valuesDevFile != null) {
                this.valuesObject = YamlUtils.createYamlObjectFromFile(this.valuesFile, MlflowValues.class, logger);
                this.valuesDevObject = YamlUtils.createYamlObjectFromFile(this.valuesDevFile, MlflowValues.class, logger);
                
                if ((this.valuesObject == null || !this.valuesObject.hasExternalS3()) && 
                    (this.valuesDevObject == null || !this.valuesDevObject.hasExternalS3())) {
                    logger.info("Found values.yaml and values-dev.yaml without external S3 configuration, proceeding with migration.");
                    return true;
                } else {
                    logger.info("Skipping migration, current values.yaml or values-dev.yaml already contain external S3 configuration.");
                }

            } else {
                logger.info("Skipping migration, could not find both values.yaml and values-dev.yaml within directory: {}",
                chartFile.getParentFile().getAbsolutePath());
            }
        } 
        return false;
    }

    /**
     * Function to check whether a Chart.yaml has a dependency on the aissemble-mlflow
     * helm chart
     * @param chartFile the Chart.yaml being read
     * @return {@link Boolean} for whether the chart meets the criteria
     */
    private Boolean isMlflowV2Chart(File chartFile) {
        // Create the helm chart java object
        Chart helmChart = YamlUtils.createYamlObjectFromFile(chartFile, Chart.class, logger);

        // verify the chart has dependencies
        if (helmChart != null && helmChart.getDependencies() != null) {

            // verify the chart has a dependency on aissemble-mlflow
            for (Dependency dependency: helmChart.getDependencies()) {
                if (StringUtils.equals(dependency.getName(), MLFLOW_V2_CHART_NAME)) {
                        logger.info("Found v2 Helm Chart with dependency '{}'", MLFLOW_V2_CHART_NAME);
                        return true;
                } 
            } 
            logger.info("Chart.yaml does not contain '{}' dependency, skipping file.", MLFLOW_V2_CHART_NAME);
        } else {
            logger.info("Chart.yaml does not contain any dependencies, skipping file.");
        }
        return false;
    }

    private File findFileInDirectory(String requestedFilename, File directory) {
        File requested = new File(directory, requestedFilename);
        return requested.exists() ? requested : null;
    }

    /**
     * Function performs the migration on the respective values.yaml and values-dev.yaml associated with the given MLFlow Chart.yaml.
     */
    @Override
    protected boolean performMigration(File chartFile) {
        // List of values to add as part of each migration
        LinkedList<String> linesToAddValues = new LinkedList<String>(Arrays.asList(
            "externalS3:",
            "existingSecret: remote-auth-config",
            "bucket: mlflow-models/mlflow-storage",
            "",
            "# Update these keys with your external S3 details and credentials defined here:",
            "# [YOUR-PROJECT]-deploy/src/main/resources/templates/sealed-secret.yaml",
            "# existingSecretAccessKeyIDKey: ",
            "# existingSecretKeySecretKey: ",
            "# host:"
        ));

        LinkedList<String> linesToAddValuesDev = new LinkedList<String>(Arrays.asList(
            "externalS3:",
            "host: \"s3-local\"",
            "port: 4566",
            "protocol: http",
            "existingSecretAccessKeyIDKey: \"AWS_ACCESS_KEY_ID\"",
            "existingSecretKeySecretKey: \"AWS_SECRET_ACCESS_KEY\""
        ));

        return migrateValuesFile(this.valuesFile, this.valuesObject, linesToAddValues) 
                && migrateValuesFile(this.valuesDevFile, this.valuesDevObject, linesToAddValuesDev);
    }

    /**
     * Function takes in the yaml file and {@link Mlflow} instance for it and appends the new content to the 
     * appropriate section of the file.
     * @param file the values yaml {@link File}
     * @param helmValuesObject {@link Mlflow} instance of the values yaml
     * @param linesToAdd values to add as part of the migration
     * @return success of the migration
     */
    private boolean migrateValuesFile(File file, MlflowValues helmValuesObject, LinkedList<String> linesToAdd) {
        logger.info("Migrating file: {}", file.getAbsolutePath());
        List<String> newFileContents = new ArrayList<>();
        List<String> originalFile;
        try {
            originalFile = FileUtils.readAllFileLines(file);
            logger.debug("Read in {} lines", originalFile.size());

            int tabSize;

            // yaml does not contain any aissemble-mlflow config, can safely append all new content to end of file
            if (helmValuesObject == null || helmValuesObject.getAissembleMlflow() == null) {
                tabSize = 2; // using the default tab size
                newFileContents.addAll(originalFile);
                newFileContents.add("aissemble-mlflow:");
                newFileContents.add(SPACE.repeat(tabSize) + "mlflow:");

                FileUtils.indentValues(linesToAdd.subList(0, 1), tabSize * 2);
                FileUtils.indentValues(linesToAdd.subList(1, linesToAdd.size()), tabSize * 3);
                newFileContents.addAll(linesToAdd);
            }
            else {
                String lineToAppend; // Determine which line we want to append after
                Boolean addMlFlowHeader = false;

                // if 'mlflow:' is not present, will need to append it as well
                if (helmValuesObject.getAissembleMlflow().getMlflow() == null) {
                    lineToAppend = "aissemble-mlflow:";
                    addMlFlowHeader = true;
                } else {
                    lineToAppend = "mlflow:";
                }

                // Iterate through the file until we find the line to append
                String lineBeingAppended = null;
                for (String line : originalFile) {

                    // check that line to append has been found and the current line isn't blank
                    if (lineBeingAppended != null && !line.isBlank()) {
                        // calculate the tab size for the appended values
                        // (leading space on current line) - (leading space on previous line)
                        tabSize = (line.length() - line.stripLeading().length()) - 
                                    (lineBeingAppended.length() - lineBeingAppended.stripLeading().length());
                        FileUtils.indentValues(linesToAdd.subList(0, 1), tabSize * 2);
                        FileUtils.indentValues(linesToAdd.subList(1, linesToAdd.size()), tabSize * 3);

                        if (addMlFlowHeader) {
                            linesToAdd.addFirst(SPACE.repeat(tabSize) + "mlflow:");
                        } 

                        //append the values
                        newFileContents.addAll(linesToAdd);
                        lineBeingAppended = null;

                    } else if (line.stripLeading().startsWith(lineToAppend)) {
                        lineBeingAppended = line;
                    }

                    newFileContents.add(line);
                }
            }
            
            FileUtils.writeFile(file, newFileContents);
            logger.debug("Wrote {} lines to file: {}", newFileContents.size(), file.getAbsolutePath());
            return true;

		} catch (IOException e) {
			logger.error("Unable to overwrite file at " + file.getAbsolutePath(), e);
		}
        return false;
    }
}
