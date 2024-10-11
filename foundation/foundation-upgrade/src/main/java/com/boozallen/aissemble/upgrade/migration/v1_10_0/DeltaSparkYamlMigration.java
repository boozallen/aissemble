package com.boozallen.aissemble.upgrade.migration.v1_10_0;

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
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * To work with Spark 3.5, the Delta Lake dependencies need to be updated to 3.2.1.  The current dependencies are 2.4.0.
 * This migration updates the version within any values.yaml files that are used to configure the
 * aissemble-spark-application-chart Helm chart and any static SparkApplication yaml files. As part of the version
 * update, the delta-core_[SCALA-VER] dependency has to be renamed to delta-spark_[SCALA_VER].
 */
public class DeltaSparkYamlMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(DeltaSparkYamlMigration.class);

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (file.getName().endsWith(".yaml")) {
            try {
                YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
                if (yaml.hasObject("sparkApp")) {
                   yaml = yaml.getObject("sparkApp");
                }
                if (yaml.hasList("spec", "deps", "packages")) {
                    List<String> pkgs = yaml.getListOfStrings("spec", "deps", "packages");
                    return pkgs.stream().anyMatch(pkg -> pkg.startsWith("io.delta:"));
                }
            } catch (YAMLException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse YAML file, likely because it is a Helm template: {}", file.getPath(), e);
                } else {
                    logger.info("Failed to parse YAML file, likely because it is a Helm template: {}", file.getName());
                }
            } catch (IOException e) {
                throw new BatonException("Failed to read YAML file.", e);
            }
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            boolean inPackages = false;
            boolean update = false;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (inPackages) {
                    if (line.startsWith("-")) {
                        update = updatePackageIfNecessary(lines, i);
                    } else {
                        inPackages = false;
                    }
                }
                if (!inPackages && "packages:".equals(line)) {
                    inPackages = true;
                }
            }
            if (update) {
                Files.write(file.toPath(), lines);
            }
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to update Delta Lake Spark jars in: " + file.getPath(), e);
        }
    }

    private static boolean updatePackageIfNecessary(List<String> lines, int i) {
        boolean updatesMade = false;
        String line = lines.get(i);
        if (line.contains(" io.delta:delta-core")) {
            String updated = line.replaceFirst("\\bio\\.delta:delta-core_(.*?):\\S+(.*)", "io.delta:delta-spark_$1:3.2.1$2");
            lines.set(i, updated);
            updatesMade = true;
        } else if(line.contains(" io.delta:delta-storage")) {
            String updated = line.replaceFirst("\\bio\\.delta:delta-storage:(?!3\\.2\\.1\\b)\\S+(.*)", "io.delta:delta-storage:3.2.1$1");
            lines.set(i, updated);
            updatesMade = true;
        } else if (line.matches("\\bio\\.delta:.*?:[012]\\..*")) { //io.delta package that is less than 3.X
            logger.warn("Delta Lake package not updated. Consider upgrading to 3.2.1: " + line);
        }
        return updatesMade;
    }
}
