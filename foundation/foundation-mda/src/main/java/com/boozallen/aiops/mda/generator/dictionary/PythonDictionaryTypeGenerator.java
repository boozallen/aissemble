package com.boozallen.aiops.mda.generator.dictionary;

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
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.python.PythonDictionaryType;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

import java.util.Map;

/**
 * Iterates through each dictionary in the metamodel and enables the generation
 * of Python code for each dictionary type.
 */
public class PythonDictionaryTypeGenerator extends AbstractPythonGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                        | Template                                               | Generated File                            |
     * |-------------------------------|--------------------------------------------------------|-------------------------------------------|
     * | pythonDictionaryTypeBase      | data-delivery-data-records/dictionary.type.base.py.vm  | dictionary/${dictionaryTypeName}_base.py  |
     * | pythonDictionaryTypeImpl      | data-delivery-data-records/dictionary.type.impl.py.vm  | dictionary/${dictionaryTypeName}.py       |
     * | pythonDictionaryTypeBaseInit  | python.init.py.vm                                      | dictionary/__init__.py                    |
     * | pythonDictionaryTypeImplInit  | python.init.py.vm                                      | dictionary/__init__.py                    |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);

        AissembleModelInstanceRepository metamodelRepository = (AissembleModelInstanceRepository) generationContext.getModelInstanceRepository();

        Map<String, Dictionary> dictionaryMap = metamodelRepository.getDictionariesByContext(metadataContext);

        String baseOutputFile = generationContext.getOutputFile();

        for (Dictionary dictionary : dictionaryMap.values()) {
            dictionary.validate();
            vc.put(VelocityProperty.DICTIONARY, dictionary);

            for (DictionaryType dictionaryType : dictionary.getDictionaryTypes()) {
                PythonDictionaryType pythonDictionaryType = new PythonDictionaryType(dictionaryType);
                // only generate if type is complex
                if (pythonDictionaryType.isComplex()) {
                    pythonDictionaryType.validate();
                    vc.put(VelocityProperty.DICTIONARY_TYPE, pythonDictionaryType);

                    String outputFile = replace("dictionaryTypeName", baseOutputFile,
                            pythonDictionaryType.getSnakeCaseName());
                    generationContext.setOutputFile(outputFile);
                    generateFile(generationContext, vc);
                }
            }
        }
    }
}
