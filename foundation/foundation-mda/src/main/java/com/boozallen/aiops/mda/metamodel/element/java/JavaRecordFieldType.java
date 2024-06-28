package com.boozallen.aiops.mda.metamodel.element.java;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.AIOpsModelInstanceRepostory;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;

/**
 * Decorates RecordFieldType with Java-specific functionality.
 */
public class JavaRecordFieldType extends BaseRecordFieldTypeDecorator {

    private AIOpsModelInstanceRepostory modelRepository = ModelInstanceRepositoryManager
            .getMetamodelRepository(AIOpsModelInstanceRepostory.class);

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
