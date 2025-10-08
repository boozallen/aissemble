package com.boozallen.aiops.mda.metamodel.element.proto;

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
