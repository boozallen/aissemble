package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.Map;

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.generator.AbstractMavenModuleGenerator;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.element.DataAccess;
import com.boozallen.aiops.mda.metamodel.element.Record;

/**
 * Iterates through each record in the metamodel and enables the generation of a
 * data-access module if needed.
 */
public class DataAccessModuleGenerator extends AbstractMavenModuleGenerator {
    /*--~-~-~~
     * Usages:
     * | Target             | Template                            | Generated File               |
     * |--------------------|-------------------------------------|------------------------------|
     * | dataAccessPomFile  | data-access/data.access.pom.xml.vm  | ${moduleArtifactId}/pom.xml  |
     */

    protected static final String APP_NAME = "data-access";

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        if (shouldGenerateModule()) {
            VelocityContext vc = getNewVelocityContext(context);

            String rootArtifactId = context.getRootArtifactId();
            String moduleArtifactId = getModuleArtifactId(context, APP_NAME);

            vc.put(VelocityProperty.ROOT_ARTIFACT_ID, rootArtifactId);
            vc.put(VelocityProperty.PARENT_ARTIFACT_ID, context.getArtifactId());
            vc.put(VelocityProperty.MODULE_ARTIFACT_ID, moduleArtifactId);
            vc.put(VelocityProperty.GROUP_ID, context.getGroupId());
            vc.put(VelocityProperty.VERSION, context.getVersion());
            vc.put(VelocityProperty.PARENT_DESCRIPTIVE_NAME, context.getDescriptiveName());
            vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());

            String basefileName = context.getOutputFile();
            String fileName = replace(VelocityProperty.MODULE_ARTIFACT_ID, basefileName, moduleArtifactId);
            context.setOutputFile(fileName);

            generateFile(context, vc);

            addBuildNotices(context, moduleArtifactId, APP_NAME);
        }
    }

    /**
     * Returns the artifact id for the module to generate.
     * 
     * @param context
     *            generation context
     * @return artifact id for the module to generate
     */
    protected String getModuleArtifactId(GenerationContext context) {
        String parentArtifactId = context.getArtifactId();
        return parentArtifactId.replace("pipelines", APP_NAME);
    }

    private boolean shouldGenerateModule() {
        boolean shouldGenerateModule = false;

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Record> recordMap = metamodelRepository.getRecordsByContext(metadataContext);
        if (!recordMap.isEmpty()) {
            for (Record record : recordMap.values()) {
                // generate if there is at least one record that has not
                // explicitly disabled data access
                DataAccess dataAccess = record.getDataAccess();
                if (dataAccess == null || dataAccess.isEnabled()) {
                    shouldGenerateModule = true;
                    break;
                }
            }
        }

        return shouldGenerateModule;
    }

}
