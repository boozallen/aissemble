package com.boozallen.aissemble.upgrade.migration.v1_9_0;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration is responsible for updating the ArgoCD values file to include the syncPolicy in order to enable
 * ArgoCD automatically sync an application ability. Ultimately, this enables the ArgoCD sync wave feature to function properly.
 */
public class ArgocdTemplateSyncPolicyMigration extends AbstractAissembleMigration {

    protected static final Logger logger = LoggerFactory.getLogger(ArgocdTemplateSyncPolicyMigration.class);
    private static final String SYNC_POLICY = "syncPolicy:";
    private static final String SPEC = "spec:";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = true;
        if (file != null && file.exists()) {
            try {
                List<String> content = readAllFileLines(file);
                if (isApplication(content)) {
                    for (String line: content) {
                        if (line.trim().equals(SYNC_POLICY)) {
                            shouldExecute = false;
                            break;
                        }
                    }
                } else {
                    shouldExecute = false;
                }
            } catch (IOException e) {
                logger.error("Failed to check content of file: {}", file.getPath(), e);
            }
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> content = readAllFileLines(file);
            List<String> updatedContent = new ArrayList<>();
            boolean startSpecConfig = false;
            int counter = 0;
            int size = content.size();
            int indentSpaces = 0;
            String specIndent = null;
            for (String line: content) {
                counter++;
                boolean lastItem = counter == size;

                line = line.stripTrailing();
                if (line.equals(SPEC)) {
                    startSpecConfig = true;
                    updatedContent.add(line);

                    // get the indentSpaces spaces
                    indentSpaces = YamlUtils.getIndentSpaces(content, counter);
                    specIndent = indent(1, indentSpaces);
                    continue;
                }

                if (startSpecConfig && !StringUtils.isBlank(line) && !line.startsWith(specIndent)) {
                    updatedContent.addAll(getSyncPolicyHelmFunc(indentSpaces));
                    startSpecConfig = false;
                }
                updatedContent.add(line);

                if (startSpecConfig && lastItem) {
                    updatedContent.addAll(getSyncPolicyHelmFunc(indentSpaces));
                }
            }

            writeFile(file, updatedContent);
        } catch (Exception e) {
            logger.error("Failed to migrate ArgoCD syncPolicy to the template file: {}", file.getPath(), e);
        }
        return false;
    }

    private List<String> getSyncPolicyHelmFunc(int indentSpaces) {
        ArrayList<String> syncPolicy = new ArrayList();
        syncPolicy.add(indent(1, indentSpaces) + "{{- with .Values.spec.syncPolicy }}");
        syncPolicy.add(indent(1, indentSpaces) + "syncPolicy:");
        syncPolicy.add(indent(2, indentSpaces) + String.format("{{- toYaml . | nindent %d }}", 2 * indentSpaces));
        syncPolicy.add(indent(1, indentSpaces) + "{{- end }}");
        return syncPolicy;
    }

    private boolean isApplication(List<String> content) {
        final String kindApplication = "kind:application";
        String line = "";
        for (int i=0; i<content.size(); i++) {
            line = content.get(i);
            if (line.toLowerCase().startsWith("kind")) {
                break;
            }
        }
        line = line.replaceAll("\\s", "");
        return line.equalsIgnoreCase(kindApplication);
    }
}
