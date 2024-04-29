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

import com.boozallen.aiops.mda.generator.AbstractPythonGenerator;
import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import com.boozallen.aiops.mda.metamodel.element.Framework;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.pyspark.PySparkRecord;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import java.util.Map;
import java.util.Optional;

/**
 * Iterates through each record in the metamodel and enables the generation of
 * Python code for each record.
 */
public class PythonRecordGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                 | Template                                            | Generated File                 |
     * |------------------------|-----------------------------------------------------|--------------------------------|
     * | pythonRecordBase       | data-delivery-data-records/record.base.py.vm        | record/${recordName}_base.py   |
     * | pythonRecordFieldEnum  | data-delivery-data-records/record.field.enum.py.vm  | record/${recordName}_field.py  |
     * | pythonRecordImpl       | data-delivery-data-records/record.impl.py.vm        | record/${recordName}.py        |
     * | pythonRecordBaseInit   | python.init.py.vm                                   | record/__init__.py             |
     * | pythonRecordImplInit   | python.init.py.vm                                   | record/__init__.py             |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Record> recordMap = metamodelRepository.getRecordsByContext(metadataContext);

        String baseOutputFile = generationContext.getOutputFile();

        for (Record currentRecord : recordMap.values()) {
            PythonRecord pythonRecord = getPythonRecord(currentRecord);
            vc.put(VelocityProperty.RECORD, pythonRecord);

            String recordOutputFileName = replace("recordName", baseOutputFile, pythonRecord.getSnakeCaseName());
            generationContext.setOutputFile(recordOutputFileName);
            generateFile(generationContext, vc);
        }
    }

    protected PythonRecord getPythonRecord(Record currentRecord) {
        Optional<Framework> frameworkOptional = currentRecord.getFrameworks()
                .stream()
                .filter(framework -> FrameworkEnum.PYSPARK.equals(framework.getName()))
                .findAny();
        if (frameworkOptional.isPresent()) {
            return new PySparkRecord(currentRecord);
        }
        return new PythonRecord(currentRecord);
    }
}
