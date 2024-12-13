package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * aiSSEMBLE::Foundation::MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.generator.util.MavenUtil;
import com.boozallen.aiops.mda.generator.util.MavenUtil.Language;
import com.boozallen.aiops.mda.generator.util.SemanticDataUtil.DataRecordModule;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * Common functionality for generating {@code pom.xml} files for all data records modules.
 */
public abstract class DataRecordsPomGenerator extends AbstractMavenModuleGenerator {

    @Override
    public void generate(GenerationContext context) {
        VelocityContext vc = getNewVelocityContext(context);

        String projectName = context.getRootArtifactId().replace("-shared", "");
        DataRecordModule generatingModule = getModuleBeingGenerated(context);
        String artifactId = MavenUtil.getDataRecordModuleName(context, metadataContext, getLanguage(), generatingModule);
        String fileName = artifactId + "/pom.xml";
        context.setOutputFile(fileName);

        vc.put(VelocityProperty.ARTIFACT_ID, artifactId);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
        vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
        vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
        vc.put(VelocityProperty.PROJECT_NAME, projectName);
        // if it's split modules, we need to know the names of the core modules to add the dependency to the spark modules
        vc.put(VelocityProperty.JAVA_DATA_RECORDS, getJavaDataRecordModule(context, DataRecordModule.CORE));
        vc.put(VelocityProperty.PYTHON_DATA_RECORDS, getPythonDataRecordModule(context, DataRecordModule.CORE));
        populateVelocityContext(context, vc);

        if (shouldGenerate(context)) {
            generateFile(context, vc);
            manualActionNotificationService.addNoticeToAddModuleToParentBuild(context, artifactId, "shared");
        }
    }

    /**
     * Controls whether the POM file is generated.
     *
     * @param context the generation context
     * @return whether the POM file should be generated
     */
    protected abstract boolean shouldGenerate(GenerationContext context);

    /**
     * Populates the specified {@code vc} with attributes that are specific to the module being generated.
     *
     * @param context the generation context
     * @param vc      the velocity context
     */
    protected abstract void populateVelocityContext(GenerationContext context, VelocityContext vc);


    /**
     * Returns the intended language of the POM so mixed-language projects can delineate between impl languages.
     *
     * @return the language of the module
     */
    protected abstract Language getLanguage();

    private DataRecordModule getModuleBeingGenerated(GenerationContext context) {
        DataRecordModule generatingModule = null;
        String targetModuleBaseName = replace("project", context.getOutputFile(), "");
        targetModuleBaseName = replace("lang", targetModuleBaseName, "");
        targetModuleBaseName = targetModuleBaseName.replace("/pom.xml", "");
        for (DataRecordModule moduleType : DataRecordModule.values()) {
            if( targetModuleBaseName.equals(moduleType.getBaseName()) ) {
                generatingModule = moduleType;
            }
        }
        if( generatingModule == null ) {
            throw new IllegalArgumentException("Could not determine the module type for the file: " + context.getOutputFile() + " [" + targetModuleBaseName + "]");
        }
        return generatingModule;
    }
}
