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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import com.boozallen.aiops.mda.generator.AbstractJavaGenerator;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.java.JavaRecord;

/**
 * Generates a java class for the data-access module.
 */
public class DataAccessJavaGenerator extends AbstractJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                  | Template                                       | Generated File                              |
     * |-------------------------|------------------------------------------------|---------------------------------------------|
     * | dataAccessResourceBase  | data-access/data.access.resource.base.java.vm  | ${basePackage}/DataAccessResourceBase.java  |
     * | dataAccessResourceImpl  | data-access/data.access.resource.impl.java.vm  | ${basePackage}/DataAccessResource.java      |
     */


    /**
     * {@inheritDoc}
     */
    @Override
    public void generate(GenerationContext context) {
        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) context.getModelInstanceRepository();

        Map<String, Record> recordMap = metamodelRepository.getRecordsByContext(metadataContext);
        List<JavaRecord> dataAccessEnabledRecords = getDataAccessEnabledRecords(recordMap);

        VelocityContext vc = getNewVelocityContext(context);
        vc.put(VelocityProperty.BASE_PACKAGE, context.getBasePackage());
        vc.put(VelocityProperty.RECORDS, dataAccessEnabledRecords);

        setOutputFileName(context);
        generateFile(context, vc);
    }

    private List<JavaRecord> getDataAccessEnabledRecords(Map<String, Record> recordMap) {
        List<JavaRecord> dataAccessEnabledRecords = new ArrayList<>();
        for (Record record : recordMap.values()) {
            if (record.getDataAccess() == null || record.getDataAccess().isEnabled()) {
                dataAccessEnabledRecords.add(new JavaRecord(record));
            }
        }

        return dataAccessEnabledRecords;
    }

}
