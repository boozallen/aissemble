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

import java.util.Map;

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.generator.AbstractJavaGenerator;
import com.boozallen.aiops.mda.generator.common.VelocityProperty;
import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.java.JavaDictionaryType;

/**
 * Iterates through each dictionary in the metamodel and enables the generation
 * of Java code for each dictionary type.
 */
public class JavaDictionaryTypeGenerator extends AbstractJavaGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                  | Template                                          | Generated File                                 |
     * |-------------------------|---------------------------------------------------|------------------------------------------------|
     * | javaDictionaryTypeBase  | data-delivery-spark/dictionary.type.base.java.vm  | ${basePackage}/${dictionaryTypeName}Base.java  |
     * | javaDictionaryTypeImpl  | data-delivery-spark/dictionary.type.impl.java.vm  | ${basePackage}/${dictionaryTypeName}.java      |
     */


    @Override
    public void generate(GenerationContext generationContext) {
        VelocityContext vc = getNewVelocityContext(generationContext);

        AIOpsModelInstanceRepostory metamodelRepository = ModelInstanceRepositoryManager
                .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

        Map<String, Dictionary> dictionaryMap = metamodelRepository.getDictionariesByContext(metadataContext);

        String baseOutputFile = generationContext.getOutputFile();

        for (Dictionary dictionary : dictionaryMap.values()) {
            dictionary.validate();
            String dictionaryPackage = dictionary.getPackage();
            vc.put(VelocityProperty.BASE_PACKAGE, dictionaryPackage);
            vc.put(VelocityProperty.DICTIONARY, dictionary);

            for (DictionaryType dictionaryType : dictionary.getDictionaryTypes()) {
                JavaDictionaryType javaDictionaryType = new JavaDictionaryType(dictionaryType);
                // only generate if type is complex
                if (javaDictionaryType.isComplex()) {
                    javaDictionaryType.validate();
                    vc.put(VelocityProperty.DICTIONARY_TYPE, javaDictionaryType);

                    String dictionaryTypeOutputFile = getOutputFileName(javaDictionaryType, dictionaryPackage,
                            baseOutputFile);
                    generationContext.setOutputFile(dictionaryTypeOutputFile);
                    generateFile(generationContext, vc);
                }
            }
        }
    }

    private String getOutputFileName(JavaDictionaryType dictionaryType, String dictionaryPackage,
            String baseOutputFile) {
        String basePackagePath = dictionaryPackage.replace(".", "/");
        String fileName = replaceBasePackage(baseOutputFile, basePackagePath);

        return replace("dictionaryTypeName", fileName, dictionaryType.getCapitalizedName());
    }

}
