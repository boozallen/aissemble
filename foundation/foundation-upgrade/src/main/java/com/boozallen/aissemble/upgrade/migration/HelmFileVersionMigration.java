package com.boozallen.aissemble.upgrade.migration;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.technologybrewery.baton.util.FileUtils.replaceInFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import com.vdurmont.semver4j.Semver;

/**
 * Migration to update the aissemble version used in the helmfile
 */
public class HelmFileVersionMigration extends AbstractAissembleMigration {

    private static final Logger logger = LoggerFactory.getLogger(HelmFileVersionMigration.class);
    private static final String VERSION_REGEX = "(aissembleVersion: )(\\d+\\.\\d+\\.\\d+(?:[\\.\\-\\d+a-zA-Z]*))";


    /**
     * Determines if migration should be executed. Will execute if the aissemble version in the values does not match
     * the desired aissemble version
     *
     * @param file values file to check
     * @return true if it needs to be updated
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;

        try (BufferedReader valuesFile = new BufferedReader((new FileReader(file)))) {
            String line;
            Pattern pattern = Pattern.compile(VERSION_REGEX, Pattern.MULTILINE);

            while ((line = valuesFile.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String currentAissembleVersion = line.substring(line.lastIndexOf(":") + 1);
                    currentAissembleVersion = StringUtils.deleteWhitespace(currentAissembleVersion);
                    Semver semver = new Semver(currentAissembleVersion);

                    if (!semver.isEqualTo(getAissembleVersion())) {
                        logger.info("Found aiSSEMBLE version in the helmfile environments file that is not equal to the project " +
                                "aiSSEMBLE version.");
                        shouldExecute = true;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Unable to load file.", e);
        }

        return shouldExecute;
    }

    /**
     * Migration will update the aissembleVersion in the helmfile values charts
     * @param file helmfile values file to migrate
     * @return true if migration was successfully performed
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated;

        // Update the aissemble to the desired version
        try {
            String substitution = "$1" + getAissembleVersion();
            isMigrated = replaceInFile(file, VERSION_REGEX, substitution);
        } catch (IOException e) {
            throw new BatonException("Could not migrate file: " + file.getPath(), e);
        }
        return isMigrated;
    }
}
