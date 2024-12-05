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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration applies the updates to Pipeline Invocation Service ArgoCD application template to include the helm.valueFiles helm function
 */
public class PipelineInvocationServiceTemplateMigration extends AbstractAissembleMigration {
    protected static final Logger logger = LoggerFactory.getLogger(PipelineInvocationServiceTemplateMigration.class);

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        if (file != null && file.exists()) {
            try {
                List<String> content = readAllFileLines(file);
                for (String line : content) {
                    if (line.contains("valueFiles:")) {
                        return false;
                    }
                }
            } catch (IOException e) {
                throw new BatonException("Failed to evaluate pipeline invocation service ArgoCD template.", e);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        List<String> content;
        List<String> updatedContent = new ArrayList<>();
        boolean inSpec = false;
        boolean updated = false;
        String sourceParam = "";
        int indentSpaces = 0;
        try {
            content = readAllFileLines(file);
            for (int i = 0; i < content.size(); i++) {
                String line = content.get(i).stripTrailing();
                updatedContent.add(line);
                if (!inSpec && "spec:".equals(line)) {
                    inSpec = true;
                    indentSpaces = YamlUtils.getIndentSpaces(content, i + 1);
                    sourceParam = indent(1, indentSpaces) + "source:";
                }
                if (!updated && inSpec && sourceParam.equals(line)) {
                    updatedContent.addAll(getHelmValuesParamFunc(indentSpaces));
                    updated = true;
                }
            }
            if (!updated) {
                throw new BatonException("No spec.resource parameters found in the template");
            }

            writeFile(file, updatedContent);
            return true;
        } catch (Exception e) {
            throw new BatonException("Failed to migrate pipeline invocation service ArgoCD template at: " + file.getPath(), e);
        }
    }



    protected List<String> getHelmValuesParamFunc(int indentSpaces) {
        ArrayList<String> syncPolicy = new ArrayList<>();
        syncPolicy.add(indent(2, indentSpaces) + "{{ if .Values.spec.helm.valueFiles }}");
        syncPolicy.add(indent(2, indentSpaces) + "helm:");
        syncPolicy.add(indent(3, indentSpaces) + "valueFiles:");
        syncPolicy.add(indent(4, indentSpaces) + "{{- range .Values.spec.helm.valueFiles }}");
        syncPolicy.add(indent(4, indentSpaces) + "- {{ . }}");
        syncPolicy.add(indent(4, indentSpaces) + "{{- end }}");
        syncPolicy.add(indent(2, indentSpaces) + "{{ end }}");
        return syncPolicy;
    }
}
