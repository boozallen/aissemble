package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.generator.common.AbstractGeneratorAissemble;
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.io.File;

/**
 * Common configuration for generating maven modules.
 */
public abstract class AbstractMavenModuleGenerator extends AbstractGeneratorAissemble {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMavenModuleGenerator.class);

    protected ManualActionNotificationService manualActionNotificationService = new ManualActionNotificationService();

    protected String deriveDescriptiveNameFromCamelCase(String camelCasedString) {
        StringBuilder artifactId = new StringBuilder();
        String[] splitStrings = StringUtils.splitByCharacterTypeCamelCase(camelCasedString);

        boolean isFirst = true;
        for (String segment : splitStrings) {

            if (isFirst) {
                isFirst = false;

            } else {
                artifactId.append(' ');

            }

            artifactId.append(StringUtils.capitalize(segment));

        }

        return artifactId.toString();
    }

    /**
     * Adds build notices for the module to generate.
     *
     * @param context
     *            generation context
     * @param moduleArtifactId
     *            the artifact of the module to generate
     */
    protected void addBuildNotices(GenerationContext context, String moduleArtifactId, String appName) {
        manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, moduleArtifactId, appName);
    }

    /**
     * Returns the artifact id for the module to generate.
     *
     * @param context
     *            generation context
     * @return artifact id for the module to generate
     */
    protected String getModuleArtifactId(GenerationContext context, String appName) {
        String parentArtifactId = context.getArtifactId();
        return parentArtifactId.replace("pipelines", appName);
    }

    @Override
    protected String getOutputSubFolder() {
        return "";
    }

    @Override
    protected File getBaseFile(GenerationContext context) {
        return context.getProjectDirectory();
    }

    protected String getJavaDataRecordModule(GenerationContext context, DataRecordModule dataModule) {
        return MavenUtil.getDataRecordModuleName(context, metadataContext, Language.JAVA, dataModule);
    }

    protected String getPythonDataRecordModule(GenerationContext context, DataRecordModule dataModule) {
        return MavenUtil.getDataRecordModuleName(context, metadataContext, Language.PYTHON, dataModule);
    }

}
