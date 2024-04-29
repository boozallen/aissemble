package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * foundation-upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.util.TiltfileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.boozallen.aissemble.upgrade.util.MigrationUtils.isLessThanVersion;
import static com.boozallen.aissemble.upgrade.util.FileUtils.replaceInFile;

public class TiltfileMigration extends AbstractAissembleMigration {
    public static final String VERSION_REGEX = "(aissemble_version = *['\"])(\\d+\\.\\d+\\.\\d+(?:[\\.\\-\\d+a-zA-Z]*))(['\"])";

    public static final Logger logger = LoggerFactory.getLogger(TiltfileMigration.class);

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    public boolean shouldExecuteOnFile(File file) {
        boolean foundNewAissembleVersion = false;
        boolean shouldExecute = false;

        try (BufferedReader tiltFileConfig = new BufferedReader( (new FileReader(file)))) {
            String line;

            while ((line = tiltFileConfig.readLine()) != null) {
                if (line.contains(TiltfileUtil.VERSION_AISSEMBLE_POST_1_5_0)) {
                    foundNewAissembleVersion = true;
                }

                String tiltfileVersionvalue;
                if ((tiltfileVersionvalue = TiltfileUtil.extractAissembleVersion(line)) != null &&
                        isLessThanVersion(tiltfileVersionvalue, getAissembleVersion())) {
                    logger.info("Found VERSION_AISSEMBLE that is less than project aiSSEMBLE version in the Tiltfile.");
                    shouldExecute = true;
                }
            }
        } catch (IOException e) {
            logger.error("Unable to load file.", e);
        }

        return shouldExecute && foundNewAissembleVersion;
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true.
     * @param file file to migrate
     * @return isMigrated - Whether the file was migrated successfully.
     */
    @Override
    public boolean performMigration(File file) {
        boolean isMigrated = false;

        // migrate past 1.5.0
        try (BufferedReader tiltFileConfig = new BufferedReader( (new FileReader(file)))) {
            String substitution = "$1" + getAissembleVersion() +"$3";
            isMigrated = replaceInFile(file, TiltfileUtil.VERSION_AISSEMBLE_POST_1_5_0_REGEX, substitution);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return isMigrated;
    }
}
