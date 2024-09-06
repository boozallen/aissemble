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
import org.technologybrewery.baton.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
/**
 * This migration is responsible for updating the ArgoCD template file to include the syncPolicy helm function in order to enable
 * ArgoCD automatically sync an application ability. Ultimately, this enables the ArgoCD sync wave feature to function properly.
 */
public class ArgocdValueFileSyncPolicyMigration extends AbstractAissembleMigration {

    protected static final Logger logger = LoggerFactory.getLogger(ArgocdValueFileSyncPolicyMigration.class);
    private static final String CREATE_NAMESPACE_OPTION = "CreateNamespace";
    private static final String APPLY_OUT_OF_SYNC_OPTION = "ApplyOutOfSyncOnly";
    private static final String AUTOMATED = "automated";
    private static final String SPEC = "spec:";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            return getMigratedSyncPolicyContent(file, 2).size() > 0;
        } catch (IOException e) {
            logger.error("Failed to check content of file: {}", file.getPath(), e);
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> content = FileUtils.readAllFileLines(file);
            List<String> updatedContent = new ArrayList<>();

            String syncPolicy = null;
            String syncPolicyIndent = null;
            String specIndent = null;
            String line;
            boolean inSpecConfig = false;
            boolean inSyncPolicyConfig = false;
            boolean skip;
            boolean lastItem;
            int size = content.size();
            int indentSpaces = 0;

            for (int i=0; i<size; i++) {
                line = content.get(i);
                lastItem = i == size -1;
                line = line.stripTrailing();

                if (line.equals(SPEC)) {
                    inSpecConfig = true;
                    // get the indentSpaces spaces
                    indentSpaces = YamlUtils.getIndentSpaces(content, i + 1);
                    syncPolicy = indent(1, indentSpaces) + "syncPolicy:";
                    syncPolicyIndent = indent(2, indentSpaces);
                    specIndent = indent(1, indentSpaces);
                }

                if (inSpecConfig && line.equals(syncPolicy)) {
                    inSyncPolicyConfig = true;
                    skip = true;
                } else {
                    skip = inSyncPolicyConfig && line.startsWith(syncPolicyIndent);
                }

                if (inSpecConfig && !line.startsWith(specIndent) && !line.equals(SPEC) && !StringUtils.isBlank(line)) {
                    updatedContent.addAll(getMigratedSyncPolicyContent(file, indentSpaces));
                    inSpecConfig = false;
                }

                if (!skip) {
                    updatedContent.add(line);
                }

                if (inSpecConfig && lastItem) {
                    updatedContent.addAll(getMigratedSyncPolicyContent(file, indentSpaces));
                }
            }
            // this is set for empty values file
            if (!updatedContent.contains(SPEC)) {
                updatedContent.addAll(getMigratedSyncPolicyContent(file, 2));
            }
            FileUtils.writeFile(file, updatedContent);
        } catch (Exception e) {
            logger.error("Failed to migrate ArgoCD syncPolicy to the value file: {}", file.getPath(), e);
        }
        return false;
    }

    private List<String> getMigratedSyncPolicyContent(File file, int indentSpace) throws IOException {

        YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
        if (!yaml.isEmpty() && yaml.containsKey("spec")) {
            YamlUtils.YamlObject spec = yaml.getObject("spec");
            if (spec.containsKey("syncPolicy")) {
                YamlUtils.YamlObject syncPolicy = spec.getObject("syncPolicy");
                if (syncPolicy.containsKey("syncOptions")) {
                    List<String> syncOptions = syncPolicy.getListOfStrings("syncOptions");

                    if (containsExpectedSyncOptions(syncOptions) && syncPolicy.containsKey(AUTOMATED)) {
                        return new ArrayList<>();
                    }

                    return generateSyncPolicyContent(syncOptions, syncPolicy, indentSpace);
                }
            }
            return generateSyncPolicyContent(new ArrayList<>(), new YamlUtils.YamlObject(new HashMap<>()), indentSpace);
        }
        List<String> syncPolicyContent = generateSyncPolicyContent(new ArrayList<>(), new YamlUtils.YamlObject(new HashMap<>()), indentSpace);
        syncPolicyContent.add(0, SPEC);
        return syncPolicyContent;
    }

    private boolean containsExpectedSyncOptions(List<String> options) {
        return options.stream().filter(line -> (line.contains(CREATE_NAMESPACE_OPTION) || line.contains(APPLY_OUT_OF_SYNC_OPTION))).count() >=2;
    }

    private List<String> generateSyncPolicyContent(List<String> options, YamlUtils.YamlObject syncPolicy, int indentSpaces) {
        List<String> content = new ArrayList<>();
        content.add(indent(1, indentSpaces) + "syncPolicy:");
        content.add(indent(2, indentSpaces) + "syncOptions:");
        content.add(indent(3, indentSpaces) + "- CreateNamespace=true");
        content.add(indent(3, indentSpaces) + "- ApplyOutOfSyncOnly=true");
        for (String option: options) {
            if (!option.contains(CREATE_NAMESPACE_OPTION) && !option.contains(APPLY_OUT_OF_SYNC_OPTION)) {
                content.add(String.format("%s- %s", indent(3, indentSpaces), option));
            }
        }
        content.addAll(generateAutomatedSyncPolicy(syncPolicy, indentSpaces));
        return content;
    }

    private List<String> generateAutomatedSyncPolicy(YamlUtils.YamlObject syncPolicy, int indentSpaces) {
        List<String> automated = new ArrayList<>();
        if (syncPolicy.containsKey(AUTOMATED)) {
            YamlUtils.YamlObject automatedYaml = syncPolicy.getObject(AUTOMATED);
            Set<String> keys = automatedYaml.keySet();
            if (keys != null && keys.size() > 0) {
                automated.add(indent(2, indentSpaces) + "automated:");
                for (String key : keys) {
                    automated.add(String.format("%s%s: %s", indent(3, indentSpaces), key, automatedYaml.get(key)));
                }
                return automated;
            }
        }

        automated.add(indent(2, indentSpaces) + "automated: {}");
        return automated;
    }

}
