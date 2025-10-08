package com.boozallen.aiops.mda.metamodel.element.java;

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

import com.boozallen.aiops.mda.metamodel.AissembleModelInstanceRepository;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;

/**
 * Decorates RecordFieldType with Java-specific functionality.
 */
public class JavaRecordFieldType extends BaseRecordFieldTypeDecorator {

    private AissembleModelInstanceRepository modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AissembleModelInstanceRepository.class);

    /**
     * {@inheritDoc}
     */
    public JavaRecordFieldType(RecordFieldType recordFieldTypeToDecorate) {
        super(recordFieldTypeToDecorate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DictionaryType getDictionaryType() {
        return new JavaDictionaryType(super.getDictionaryType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackage() {
        return StringUtils.isNotBlank(super.getPackage()) ? super.getPackage() : modelRepository.getBasePackage();
    }

}
