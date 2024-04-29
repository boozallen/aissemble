package com.boozallen.aiops.mda.metamodel.element.proto;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;

/**
 * Decorates {@link RecordFieldType}s with Protobuf-specific generation functionality.
 */
public class ProtobufRecordFieldType extends BaseRecordFieldTypeDecorator {

    /**
     * New decorator for {@link RecordFieldType}.
     *
     * @param recordFieldTypeToDecorate instance to decorate
     */
    public ProtobufRecordFieldType(RecordFieldType recordFieldTypeToDecorate) {
        super(recordFieldTypeToDecorate);
    }

    @Override
    public DictionaryType getDictionaryType() {
        return new ProtobufDictionaryType(super.getDictionaryType());
    }
}
