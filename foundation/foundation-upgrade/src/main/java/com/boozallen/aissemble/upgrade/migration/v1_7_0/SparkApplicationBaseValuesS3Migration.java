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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.pojo.SparkApplication;
import com.boozallen.aissemble.upgrade.pojo.SparkApplication.Env;
import com.boozallen.aissemble.upgrade.util.FileUtils;
import com.boozallen.aissemble.upgrade.util.YamlUtils;

/**
 * Migration to update S3 configuration within the base values of a SparkApplication to use a secret.
 */
public class SparkApplicationBaseValuesS3Migration extends AbstractAissembleMigration {

    protected static final String SPACE = " ";
    protected static final Logger logger = LoggerFactory.getLogger(SparkApplicationBaseValuesS3Migration.class);

    /**
     * Function to determine whether a SparkApplication base values contains Localstack configuration that we would like to migrate.
     */
    @Override
    protected boolean shouldExecuteOnFile(File sparkApplicationBaseValuesFile) {
        SparkApplication baseValuesObject = YamlUtils.createYamlObjectFromFile(sparkApplicationBaseValuesFile, SparkApplication.class, logger);

        // check that the base values file contains localstack configuration
        if (hasLocalstackConfig(baseValuesObject.getDriverEnvs()) || 
                hasLocalstackConfig(baseValuesObject.getExecutorEnvs())) {
            logger.info("Found base values with localstack configuration, proceeding with migration.");
            return true;
        } else {
            logger.info("Skipping migration, did not find localstack configuration in the driver or executor environment variables");
        }
        return false;
    }

    /**
     * Determines whether any of the environment variables in the list contain Localstack configuration.
     * @param envs List of environment variables
     * @return {@link Boolean}
     */
    protected boolean hasLocalstackConfig(List<Env> envs) {
        Boolean hasLocalstackAccessKey, hasLocalstackSecretKey;
        hasLocalstackAccessKey = hasLocalstackSecretKey = false;

        // check there are envs and iterate through them
        if (envs != null && envs.size() > 0) {
            for (Env env: envs) {
                // check for the default localstack config values
                if (StringUtils.equals(env.getName(), "AWS_ACCESS_KEY_ID") && StringUtils.equals(env.getValue(), "123")) {
                    hasLocalstackAccessKey = true;
                } else if (StringUtils.equals(env.getName(), "AWS_SECRET_ACCESS_KEY") && StringUtils.equals(env.getValue(), "456")) {
                    hasLocalstackSecretKey = true;
                }
            }
        }

        return hasLocalstackAccessKey && hasLocalstackSecretKey;
    }


    /**
     * Function to migrate the driver and executor environment variables from the hardcoded LocalStack config to use secrets.
     * @return success of the migration
     */
    @Override
    protected boolean performMigration(File sparkApplicationBaseValuesFile) {
        logger.info("Migrating file: {}", sparkApplicationBaseValuesFile.getAbsolutePath());
        SparkApplication baseValuesObject = YamlUtils.createYamlObjectFromFile(sparkApplicationBaseValuesFile, SparkApplication.class, logger);

        // Old yaml elements to remove
        List<String> envsToRemove = new ArrayList<>(Arrays.asList(
            "AWS_ACCESS_KEY_ID",
            "AWS_SECRET_ACCESS_KEY",
            "STORAGE_ENDPOINT"
        ));
        Boolean removeCurrentLine = false;

        // New yaml elements to add to add
        List<String> instructionComment = new ArrayList<>(Arrays.asList(
            "# Setup these secret key references within your SealedSecret"
        ));
        List<String> envFromContents = new ArrayList<>(Arrays.asList(
            "- secretRef:",
            SPACE.repeat(4) + "name: remote-auth-config"
        ));
        
        // Determine which lines we want to append after
        List<String> linesToAppend = new ArrayList<String>(); 
        String lineBeingAppended = null;
        Boolean hasInstructionComment, appendDriverEnvFrom, appendExecutorEnvFrom;
        hasInstructionComment = appendDriverEnvFrom = appendExecutorEnvFrom = false;

        if (hasLocalstackConfig(baseValuesObject.getDriverEnvs())) {
            if (baseValuesObject.hasDriverEnvFrom()) {
                linesToAppend.add("envFrom:");
                appendDriverEnvFrom = true;
            } else {
                linesToAppend.add("driver:");
            }
        }
        
        if (hasLocalstackConfig(baseValuesObject.getExecutorEnvs())) {
            if (baseValuesObject.hasExecutorEnvFrom()) {
                linesToAppend.add("envFrom:");
                appendExecutorEnvFrom= true;
            } else {
                linesToAppend.add("executor:");
            }
        }


        Boolean isDriver, isExecutor;
        isDriver = isExecutor = false;

        List<String> newFileContents = new ArrayList<>();
        List<String> originalFile;

        try {
            originalFile = FileUtils.readAllFileLines(sparkApplicationBaseValuesFile);

            // Iterate through the file
            for (String line: originalFile) {

                // Check the current line to see if it's one of interest
                if (!removeCurrentLine && lineBeingAppended == null && !line.isBlank()) {
                    
                    // Check if we are in the driver or executor sections
                    if (line.stripLeading().startsWith("driver:")) {
                        isDriver = true;
                        isExecutor = false;
                    } else if (line.stripLeading().startsWith("executor:")) {
                        isExecutor = true;
                        isDriver = false;
                    }

                    // Check for beginning of an env key/value pair to remove
                    if (envsToRemove.stream().anyMatch(str -> line.stripLeading().startsWith("-") && line.contains(str))) {
                        logger.debug("Found environment variable to remove: {}", line.strip());
                        removeCurrentLine = true;
                    } 
                    // Check if we're on a line we want to append
                    else if (linesToAppend.stream().anyMatch(str -> line.stripLeading().startsWith(str))) {

                        // Check if we're at the correct envFrom header we want to append - could be driver or executor
                        // If it's not envFrom then good to append regardless
                        if ((line.stripLeading().startsWith("envFrom:") && ((isDriver && appendDriverEnvFrom) || (isExecutor && appendExecutorEnvFrom)))
                            || !line.stripLeading().startsWith("envFrom:")) {
                            lineBeingAppended = line;
                            logger.debug("Found line to append: '{}'", line.trim());
                        }
                    }
                }
                // check that line to append has been found and the current line isn't blank
                else if (lineBeingAppended != null && !line.isBlank()) {
                    int leadingWhitespace = line.length() - line.stripLeading().length();
                    List<String> envFromContentsCopy = new ArrayList<>(envFromContents); // create a copy as indentation could vary by location

                    // add the new instructions for using the secret
                    if (!hasInstructionComment) {
                        logger.debug("Adding instruction comment");
                        FileUtils.indentValues(instructionComment, leadingWhitespace);
                        newFileContents.addAll(instructionComment);
                        hasInstructionComment = true;
                    }

                    // Add the envFrom header if needed
                    if (!lineBeingAppended.stripLeading().startsWith("envFrom:")) {
                        logger.debug("Adding envFrom yaml header");
                        newFileContents.add(SPACE.repeat(leadingWhitespace) + "envFrom:");

                        // add default tab size of 2 spaces within the envFrom section
                        FileUtils.indentValues(envFromContentsCopy, leadingWhitespace + 2);
                    } else {
                        // assume the white space of other entries already within the envFrom section
                        FileUtils.indentValues(envFromContentsCopy, leadingWhitespace);
                    }

                    logger.debug("Adding envFrom secretRef");
                    newFileContents.addAll(envFromContentsCopy);

                    lineBeingAppended = null;
                }

                // Check for end of env key/value pair to remove
                if(removeCurrentLine) {
                    if (!line.stripLeading().startsWith("value:")) {
                        continue; // remove any comments/blank lines between the env '- name:' and 'value:' lines
                    } else {
                        removeCurrentLine = false;
                        logger.debug("Finished removing the environment variable");
                    }
                } else {
                    newFileContents.add(line);
                }
                
            }

            FileUtils.writeFile(sparkApplicationBaseValuesFile, newFileContents);
            logger.debug("Wrote {} lines to file: {}", newFileContents.size(), sparkApplicationBaseValuesFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Unable to overwrite file at " + sparkApplicationBaseValuesFile.getAbsolutePath(), e);
        }
        return false;
    }
}