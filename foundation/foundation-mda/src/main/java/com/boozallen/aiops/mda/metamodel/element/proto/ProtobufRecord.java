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

import com.boozallen.aiops.mda.metamodel.element.BaseRecordDecorator;
import com.boozallen.aiops.mda.metamodel.element.Record;
import com.boozallen.aiops.mda.metamodel.element.RecordField;

import java.util.ArrayList;
import java.util.List;

/**
 * Decorates {@link Record}s with Protobuf-specific generation functionality.
 */
public class ProtobufRecord extends BaseRecordDecorator {

    /**
     * New decorator for {@link Record}.
     *
     * @param recordToDecorate instance to decorate
     */
    public ProtobufRecord(Record recordToDecorate) {
        super(recordToDecorate);
    }

    @Override
    public List<RecordField> getFields() {
        List<RecordField> fields = new ArrayList<>();

        for (int iter = 0; iter < super.getFields().size(); iter++) {
            fields.add(new ProtobufRecordField(super.getFields().get(iter), iter + 1));
        }

        return fields;
    }
}
