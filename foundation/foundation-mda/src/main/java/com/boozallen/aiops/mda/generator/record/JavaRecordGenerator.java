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

import com.boozallen.aiops.mda.generator.AbstractJavaGenerator;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecord;

/**
 * Iterates through each record in the metamodel and enables the generation of
 * Java code for each record.
 */
public class JavaRecordGenerator extends AbstractJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target               | Template                                        | Generated File                          |
     * |----------------------|-------------------------------------------------|-----------------------------------------|
     * | javaRecordBase       | data-delivery-data-records/record.base.java.vm  | ${basePackage}/${recordName}Base.java   |
     * | javaRecordImpl       | data-delivery-data-records/record.impl.java.vm  | ${basePackage}/${recordName}.java       |
     * | javaRecordFieldEnum  | data-delivery-spark/record.field.enum.java.vm   | ${basePackage}/${recordName}Field.java  |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Record> recordMap = metamodelRepository.getRecordsByContext(metadataContext);

        String baseOutputFile = generationContext.getOutputFile();

        for (Record currentRecord : recordMap.values()) {
            if (shouldGenerateFile(currentRecord)) {
                JavaRecord javaRecord = getJavaRecord(currentRecord);
                vc.put(VelocityProperty.BASE_PACKAGE, javaRecord.getPackage());
                vc.put(VelocityProperty.RECORD, javaRecord);
    
                String recordOutputFileName = getOutputFileName(baseOutputFile, currentRecord);
                generationContext.setOutputFile(recordOutputFileName);
                generateFile(generationContext, vc);
            }
        }
    }

    protected JavaRecord getJavaRecord(Record currentRecord) {
        return new JavaRecord(currentRecord);
    }

    protected String getOutputFileName(String baseOutputFile, Record currentRecord) {
        String basePackagePath = currentRecord.getPackage().replace(".", "/");
        String fileName = replaceBasePackage(baseOutputFile, basePackagePath);

        return replace("recordName", fileName, currentRecord.getName());
    }

    /**
     * Whether the file should be generated for a record.
     * 
     * @param currentRecord
     *            the record to check
     * @return true if the file should be generated for a record
     */
    protected boolean shouldGenerateFile(Record currentRecord) {
        return true;
    }

}
