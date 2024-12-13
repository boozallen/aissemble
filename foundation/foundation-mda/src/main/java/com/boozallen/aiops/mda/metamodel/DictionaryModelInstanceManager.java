package com.boozallen.aiops.mda.metamodel;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;

import org.technologybrewery.fermenter.mda.metamodel.AbstractMetamodelManager;

import com.boozallen.aiops.mda.metamodel.element.Dictionary;
import com.boozallen.aiops.mda.metamodel.element.DictionaryElement;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;

/**
 * Responsible for maintaining the list of dictionary model instances elements in the system.
 */
class DictionaryModelInstanceManager extends AbstractMetamodelManager<Dictionary> {

    private static final DictionaryModelInstanceManager instance = new DictionaryModelInstanceManager();

    private Map<String, DictionaryType> dictionaryTypesByFullyQualifiedName = new HashMap<>();

    /**
     * Returns the singleton instance of this class.
     * 
     * @return singleton
     */
    public static DictionaryModelInstanceManager getInstance() {
        return instance;
    }

    /**
     * Prevent instantiation of this singleton from outside this class.
     */
    private DictionaryModelInstanceManager() {
        super();
    }

    @Override
    protected String getMetadataLocation() {
        return "dictionaries";
    }

    @Override
    protected Class<DictionaryElement> getMetamodelClass() {
        return DictionaryElement.class;
    }

    @Override
    protected String getMetamodelDescription() {
        return Dictionary.class.getSimpleName();
    }

    @Override
    protected void postLoadMetamodel() {
        for (Dictionary dictionary : getMetadataElementWithoutPackage().values()) {
            String dictionaryPackage = dictionary.getPackage();
            for (DictionaryType dictionaryType : dictionary.getDictionaryTypes()) {
                String fullyQualifiedName = dictionaryPackage + "." + dictionaryType.getName();
                dictionaryTypesByFullyQualifiedName.put(fullyQualifiedName, dictionaryType);
            }
        }

        super.postLoadMetamodel();
    }

    /**
     * Returns all {@link DictionaryType} instances by fully qualified name (package + . + name).
     * 
     * @return map indexed by fully qualified names
     */
    public Map<String, DictionaryType> getDictionaryTypesByFullyQualifiedName() {
        return dictionaryTypesByFullyQualifiedName;
    }

}
