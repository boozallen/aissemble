package com.boozallen.aiops.mda.generator.record;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.boozallen.aiops.mda.generator.AbstractPythonGenerator;
import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Framework;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.pyspark.PySparkRecord;
import com.boozallen.aiops.mda.metamodel.element.python.PythonRecord;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

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

        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();

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
