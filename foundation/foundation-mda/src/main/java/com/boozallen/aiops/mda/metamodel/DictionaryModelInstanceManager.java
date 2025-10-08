package com.boozallen.aiops.mda.metamodel;

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

    private static final ThreadLocal<DictionaryModelInstanceManager> instance = ThreadLocal.withInitial(DictionaryModelInstanceManager::new);

    private Map<String, DictionaryType> dictionaryTypesByFullyQualifiedName = new HashMap<>();

    /**
     * Returns the singleton instance of this class.
     * 
     * @return singleton
     */
    public static DictionaryModelInstanceManager getInstance() {
        return instance.get();
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
