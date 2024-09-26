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
import org.technologybrewery.baton.BatonException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static com.boozallen.aissemble.upgrade.util.YamlUtils.isComment;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * This migration applies the updates to syncPolicy to ArgoCD applications required for the syncWave feature for all
 * ArgoCD application templates.
 */
public class ArgocdTemplateSyncPolicyMigration extends AbstractAissembleMigration {
    protected static final Logger logger = LoggerFactory.getLogger(ArgocdTemplateSyncPolicyMigration.class);
    public static final String MIGRATION_COMMENT = "{{- /* SKIP-MIGRATION: 'argocd-template-sync-policy-configuration-migration' */}}";
    public static final String SYNC_OPTIONS = "syncOptions:";
    protected static final String SYNC_POLICY = "syncPolicy:";
    protected static final String SPEC = "spec:";
    private boolean hasSyncPolicy;
    private int indentSize;

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        boolean shouldExecute = false;
        hasSyncPolicy = false;
        indentSize = 2;
        if (file != null && file.exists()) {
            try {
                List<String> content = readAllFileLines(file);
                shouldExecute = isApplication(content);
                for (String line : content) {
                    if (!hasSyncPolicy && line.contains(SYNC_POLICY)) {
                        hasSyncPolicy = true;
                    } else if (shouldExecute && line.contains(MIGRATION_COMMENT)) {
                        shouldExecute = false;
                    }
                }
            } catch (IOException e) {
                throw new BatonException("Failed to evaluate ArgoCD template at: " + file.getPath(), e);
            }
        }
        return shouldExecute;
    }

    @Override
    protected boolean performMigration(File file) {
        if (!hasSyncPolicy) {
            return migrateWithoutSyncPolicy(file);
        } else {
            return migrateWithSyncPolicy(file);
        }
    }

    protected boolean migrateWithoutSyncPolicy(File file) {
        try {
            List<String> content = readAllFileLines(file);
            List<String> updatedContent = new ArrayList<>();
            boolean startSpecConfig = false;
            int counter = 0;
            int size = content.size();
            int indentSpaces = 0;
            String specIndent = null;
            for (String line : content) {
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

                if (startSpecConfig && isNewSection(specIndent, line)) {
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
            throw new BatonException("Failed to migrate ArgoCD syncPolicy to the template file: " + file.getPath(), e);
        }
        return true;
    }

    private boolean migrateWithSyncPolicy(File file) {
        List<String> content;
        List<String> updatedContent = new ArrayList<>();
        try {
            content = readAllFileLines(file);
        } catch (Exception e) {
            throw new BatonException("Failed to load ArgoCD template at: " + file.getPath(), e);
        }
        indentSize = YamlUtils.getIndentSpaces(content, 0);
        boolean unsupportedFormat = false;
        for (int i = 0; i < content.size(); i++) {
            String line = content.get(i);
            updatedContent.add(line);
            if (line.contains(SYNC_POLICY)) {
                //returns the index of where to pick back up on copying, but we will increment i at the end of this loop so subtract one
                i = updateSyncPolicyContent(content, updatedContent, i + 1) - 1;
                if (i < 0) {
                    unsupportedFormat = true;
                    break;
                }
            }
        }
        try {
            writeFile(file, updatedContent);
        } catch (IOException e) {
            throw new BatonException("Failed to write updated ArgoCD application at: " + file.getPath(), e);
        }
        return !unsupportedFormat;
    }

    private int updateSyncPolicyContent(List<String> content, List<String> updatedContent, int i) {
        String indent = null;
        boolean inSyncOptions = false;
        boolean unsupportedFormat = false;
        int optionListIndent = indentSize;
        updatedContent.add(getAppendingTemplateStart(indentSize, true));
        for (; i < content.size(); i++) {
            String line = content.get(i);
            String trimmed = line.trim();
            if(indent != null && isNewSection(indent, line)) {
                break;
            }
            updatedContent.add(line);
            if (trimmed.startsWith(SYNC_OPTIONS)) {
                inSyncOptions = true;
                indent = StringUtils.repeat(' ', YamlUtils.getIndentSpaces(content, i));
            } else if (inSyncOptions && trimmed.startsWith("-")) {
                optionListIndent = line.indexOf('-');
                // These are going to be set by the values file migration
                if (trimmed.startsWith("- CreateNamespace=true") || trimmed.startsWith("- ApplyOutOfSyncOnly=true")) {
                    updatedContent.remove(updatedContent.size() - 1);
                }
            } else if (!isComment(trimmed) && !line.isBlank()) {
                unsupportedFormat = true;
                i++; //we already added the line and we're about to break out of the loop
                break;
            }
        }
        // if SyncPolicy was defined but never SyncOptions -- this indicates an unsupported upgrade case
        if (unsupportedFormat || !inSyncOptions) {
            processUnsupportedFormat(content, updatedContent);
            return -1;
        } else {
            updatedContent.add(getAppendingTemplateEnd(indentSize, optionListIndent));
            return i;
        }
    }

    private void processUnsupportedFormat(List<String> content, List<String> updatedContent) {
        updatedContent.clear();
        updatedContent.addAll(content);
        updatedContent.add(getManualInstructions());
    }

    protected List<String> getSyncPolicyHelmFunc(int indentSpaces) {
        ArrayList<String> syncPolicy = new ArrayList<>();
        syncPolicy.add(indent(1, indentSpaces) + MIGRATION_COMMENT);
        syncPolicy.add(indent(1, indentSpaces) + "{{- with .Values.spec.syncPolicy }}");
        syncPolicy.add(indent(1, indentSpaces) + "syncPolicy:");
        syncPolicy.add(indent(2, indentSpaces) + String.format("{{- toYaml . | nindent %d }}", 2 * indentSpaces));
        syncPolicy.add(indent(1, indentSpaces) + "{{- end }}");
        return syncPolicy;
    }

    private static String getAppendingTemplateStart(int indentSize, boolean includeComment) {
        String content = "";
        if (includeComment) {
            content = indent(1, indentSize) + MIGRATION_COMMENT + "\n";
        }
        return content +
                indent(1, indentSize) + "{{- with .Values.spec.syncPolicy }}\n" +
                indent(2, indentSize) + "{{- if .automated }}\n" +
                indent(2, indentSize) + "automated:\n" +
                indent(3, indentSize) + "{{- toYaml .automated | nindent 6}}\n" +
                indent(2, indentSize) + "{{- else if hasKey . \"automated\" }}\n" +
                indent(2, indentSize) + "automated: {}\n" +
                indent(2, indentSize) + "{{- end }}\n" +
                indent(1, indentSize) + "{{- end }}";
    }

    private static String getAppendingTemplateEnd(int indentSize, int listIndent) {
        return indent(2, indentSize) + "{{- if .Values.spec.syncPolicy }}\n" +
                indent(3, indentSize) + "{{- with .Values.spec.syncPolicy.syncOptions }}\n" +
                indent(4, indentSize) + "{{- toYaml . | nindent " + listIndent + " }}\n" +
                indent(3, indentSize) + "{{- end }}\n" +
                indent(2, indentSize) + "{{- end }}";
    }

    private static String getManualInstructions() {
        return "\n{{- /*\n" +
                "WARNING: To update template manually, ensure the spec.syncPolicy.syncOptions and spec.syncPolicy.automated values are\n" +
                "included in the template logic. This allows the syncWave feature of ArgoCD to be properly utilized for sequencing\n" +
                "Configuration Store deployment before other apps. Example template logic:\n\n" +
                "  syncPolicy:\n" +
                getAppendingTemplateStart(2, false) + "\n" +
                "    syncOptions:\n" +
                "    - ServerSideApply=true\n" +
                getAppendingTemplateEnd(2, 4) + "\n\n" +
                "To ignore the file during this migration and suppress this comment, retain the comment below anywhere in the file: */}}\n" +
                "  " + MIGRATION_COMMENT;
    }

    private static boolean isApplication(List<String> content) {
        final String kindApplication = "kind:application";
        String line = "";
        for (int i = 0; i < content.size(); i++) {
            line = content.get(i);
            if (line.toLowerCase().startsWith("kind")) {
                break;
            }
        }
        line = line.replaceAll("\\s", "");
        return line.equalsIgnoreCase(kindApplication);
    }

    private static boolean isNewSection(String sectionIndent, String line) {
        // not blank, not a function, not a comment, and not at the correct indent level
        return !StringUtils.isBlank(line)
                && !YamlUtils.isHelmFunction(line.trim())
                && !YamlUtils.isComment(line.trim())
                && !line.startsWith(sectionIndent);
    }
}
