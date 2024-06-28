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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static com.boozallen.aissemble.upgrade.util.FileUtils.replaceLiteralInFile;

/**
 Baton migration class to verify whether a migration to the Policy Decision Point (Python) module files is required from
 the aiops reference refactor.
 **/
public class AiopsReferencePDPPythonMigration extends AbstractAissembleMigration {

    public static final Logger logger = LoggerFactory.getLogger(AiopsReferencePDPPythonMigration.class);
    public static final Map<String, String> AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP = Map.of(
            "policy-decision-point.aiops-security.properties.vm", "policy-decision-point.aissemble-security.properties.vm",
            "aiops-security.properties", "aissemble-security.properties",
            "aiops.authority","aissemble.authority",
            "aiopskey","aissemblekey",
            "/deployments/aissemble-secure.jks","/deployments/aissemble-secure.jks"
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
                shouldExecute = true;
            } else {
                BufferedReader aiopsReferencePythonDockerfileConfig = new BufferedReader((new FileReader(file)));

                String line;
                while((line = aiopsReferencePythonDockerfileConfig.readLine()) !=null && !shouldExecute) {
                    for (String key : AIOPS_REFERENCE_PYTHON_PROPERTIES_FILE_PACKAGE_MAP.keySet()) {
                        if (line.contains(key)) {
                            shouldExecute = true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error in determining whether a 'AIOPS' renamed properties file requires a migration.");
        }

        return shouldExecute;
    }

    /**
     * Renmaes a given file.
     * @param file file that will be getting refactored.
     * @param refactoredName name in which the file will be refactored to.
     * @return isRefactoredSuccessfully - Whether the file has been refactored successfully.
     */
    private static boolean fileNameRefactor(File file, String refactoredName) {
        boolean isRefactoredSuccessfully = false;
        try {
            // Construct new file path
            File renamedFile = new File(file.getParent(), refactoredName);

            // Rename the file
            isRefactoredSuccessfully = file.renameTo(renamedFile);
        } catch (Exception e) {
            logger.error("Error in refactoring aiops-security.properties.");
        }

        return isRefactoredSuccessfully;
    }

    /**
     * Performs migration.
     * @param file file to migrate
     * @return isMigrated - Whether the file was migrated successfully.
     */
    @Override
    protected boolean performMigration(File file) {
        boolean isMigrated = false;

        try {
            if(file.getName().equals("aiops-security.properties")) {
                fileNameRefactor(file, "aissemble-security.properties");
            }

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
            logger.error("Error in migrating renamed 'AIOPS' python properties file.");
        }
        return isMigrated;
    }
}