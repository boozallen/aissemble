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
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import com.boozallen.aissemble.upgrade.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.pojo.AbstractYamlObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SparkMemoryUpgradeMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkMemoryUpgradeMigration.class);
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            YamlUtils.YamlObject yamlObject = YamlUtils.loadYaml(file);
            YamlUtils.YamlObject specSection = yamlObject.getObject("sparkApp").getObject("spec");
            // Navigate through the YAML structure to access, driver memory, and executor memory
            String driverMemory = null;
            String executorMemory = null;
            if (specSection.getObject("driver") != null) {
                driverMemory = specSection.getObject("driver").getString("memory");
            }
            if (specSection.getObject("executor") != null) {
                executorMemory = specSection.getObject("executor").getString("memory");
            }

            // Check if any of the memory values are not as expected
            if ("512m".equals(driverMemory) && "512m".equals(executorMemory)) {
                logger.info("Performing migration for file: {}", file.getName());
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Error reading file or parsing YAML: {}", e.getMessage());
            return false; // Exit if unable to read file or parse YAML
        }
    }

    @Override
    protected boolean performMigration(File file) {
        return migrateValuesFile(file);
    }

    private boolean migrateValuesFile(File file) {
        try {
            List<String> lines = FileUtils.readAllFileLines(file);

            // Define the old default values to replace
            String oldDefaultValue = "512m";
            // Define the new value to replace with
            String newValue = "4096m";
            
            // Replace old default values with new value
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.stripLeading().startsWith("memory:") && line.contains(oldDefaultValue)) {
                    String updatedLine = line.replace(oldDefaultValue, newValue);
                    lines.set(i, updatedLine);
                }
            }

            // Write the updated lines back to the file
            FileUtils.writeFile(file, lines);
            logger.info("Memory values updated successfully in file: {}", file.getName());
            return true; // Indicate successful migration
        } catch (IOException e) {
            logger.error("Error updating memory values in file {}: {}", file.getName(), e.getMessage());
            return false; // Indicate migration failure
        }
    }
}