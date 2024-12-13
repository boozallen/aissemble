package com.boozallen.aissemble.upgrade.migration.v1_11_0;

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
import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.technologybrewery.baton.util.pom.PomHelper;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * This migration update the pipeline template's image configuration to use the `project version` tag
 */
public class SparkWorkerDockerImageTagMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkWorkerDockerImageTagMigration.class);

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (file.getName().endsWith(".yaml")) {
            try {
                YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
                String image = getSpecImageContent(yaml, file);
                return image!= null && (image.endsWith("latest") || image.endsWith("-spark-worker-docker"));
            } catch (YAMLException e) {
                logger.info("Failed to parse YAML file ", e);
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
            boolean inSpec = false;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (inSpec && line.startsWith("image:")) {
                    line = lines.get(i).replaceAll("(^.+)(-spark-worker-docker)(:latest)?", "$1$2:" + getRootProject().getVersion());
                    lines.set(i, line);
                    break;
                }
                if (!inSpec && "spec:".equals(line)) {
                    inSpec = true;
                }
            }
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            throw new BatonException("Failed to update -spark-worker-docker image version in: " + file.getPath(), e);
        }
        return true;
    }

    private String getSpecImageContent(YamlUtils.YamlObject yaml, File file) {
        try {
            if (yaml.hasObject("sparkApp")) {
                yaml = yaml.getObject("sparkApp");
                return yaml.getString("spec", "image");
            }
        } catch (Exception e) {
            logger.warn("Missing spec.image param in: " + file.getPath(), e);
        }

        return null;
    }
}
