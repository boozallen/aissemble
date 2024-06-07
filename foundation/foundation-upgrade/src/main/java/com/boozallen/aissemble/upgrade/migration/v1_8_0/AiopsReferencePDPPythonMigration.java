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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static com.boozallen.aissemble.upgrade.util.FileUtils.replaceLiteralInFile;

/**
 Baton migration class to verify whether a migration to the Policy Decision Point (Python) module files is required from
 the aiops reference refactor.
 **/
public class AiopsReferencePDPPythonMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(AiopsReferencePDPPythonMigration.class);
    public static final Map<String, String> AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP = Map.of(
            "policy-decision-point.aiops-security.properties.vm", "policy-decision-point.aissemble-security.properties.vm"
            );

    /**
     * Function to check whether the migration is necessary.
     * @param file file to check
     * @return shouldExecute - whether the migration is necessary.
     */
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;

        try {
            if(file.getName().equals("aiops-security.properties")) {
                BufferedReader aiopsReferencePythonPropertiesFileConfig = new BufferedReader((new FileReader(file)));

                String lineOne;
                while((lineOne = aiopsReferencePythonPropertiesFileConfig.readLine()) !=null && !shouldExecute) {
                    for (String key : AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP.keySet()) {
                        if (lineOne.contains(key)) {
                            shouldExecute = true;
                        }
                    }
                }
            } else {
                BufferedReader aiopsReferencePythonDockerfileConfig = new BufferedReader((new FileReader(file)));

                String lineTwo;
                while((lineTwo = aiopsReferencePythonDockerfileConfig.readLine()) !=null && !shouldExecute) {
                    for (String key : AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP.keySet()) {
                        if (lineTwo.contains(key)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in determining whether a 'AIOPS' renamed properties file requires a migration.");
            throw new BatonException(e);
        }

        return shouldExecute;
    }

    private static void fileNameRefactor(Path oldPath, Path newPath) {
        try {
            Files.move(oldPath, newPath);
        } catch (IOException e) {
            logger.error("Error in refactoring the file in AiopsReferencePythonPropertiesFileMigration.");
            throw new BatonException(e);
        }
    }

    /**
     * Performs the migration if the shouldExecuteOnFile() returns true.
     * @param file file to migrate
     * @return isMigrated - Whether the file was migrated successfully.
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated = false;

        try {
            if(file.getName().equals("aissemble-security.properties")) {
                for (Map.Entry<String, String> entry : AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP.entrySet()) {
                    replaceLiteralInFile(file, entry.getKey(), entry.getValue());
                }
            }

            if(file.getName().equals("Dockerfile")) {
                for (Map.Entry<String, String> entry : AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP.entrySet()) {
                    replaceLiteralInFile(file, entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            throw new BatonException("Error in migrating renamed 'AIOPS' python properties file.", e);
        }
        return isMigrated;
    }
}