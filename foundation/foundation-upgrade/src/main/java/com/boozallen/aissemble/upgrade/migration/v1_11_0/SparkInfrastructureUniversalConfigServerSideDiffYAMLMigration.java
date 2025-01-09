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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration removes hive username in the hive-metastore-service values.yaml to use values from configuration store service only if values.yaml has default value.
 */
public class SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkInfrastructureUniversalConfigServerSideDiffYAMLMigration.class);
    private static final String KIND = "kind: Application";
    private static final String NAME = "name: spark-infrastructure";
    private static final String ANNOTATIONS = "annotations:";
    private static final String SERVERSIDE_DIFF_VALUES = "argocd.argoproj.io/compare-options: ServerSideDiff=true,IncludeMutationWebhook=true";
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean isApplicationKind = false;
        boolean isNameSparkInfra = false;
        if (file != null && file.exists()) {
            try {
                List<String> content = readAllFileLines(file);
                for (String line : content) {
                    if (line.trim().contains(KIND.trim())) {
                        isApplicationKind = true;
                    }
                    if(line.trim().contains(NAME.trim())){
                        isNameSparkInfra = true;
                    }
                }
            } catch (IOException e) {
                throw new BatonException("Failed to evaluate pipeline invocation service ArgoCD template.", e);
            }
            return isApplicationKind && isNameSparkInfra;
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> updatedLine = new ArrayList<>();
            List<String> lines = readAllFileLines(file);
            int indentSpaces = 0;
            for (int i = 0; i < lines.size(); i++) {
                String trimmedLine = lines.get(i).trim();
                updatedLine.add(lines.get(i));
                if(trimmedLine.contains("namespace: argocd"))
                {
                    indentSpaces = YamlUtils.getIndentSpaces(lines, i + 1);
                    updatedLine.add(indent(1, indentSpaces) + ANNOTATIONS);
                    updatedLine.add(indent(2, indentSpaces) + SERVERSIDE_DIFF_VALUES);
                }
            }

            writeFile(file, updatedLine);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
