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

import com.boozallen.aiops.mda.metamodel.element.BaseRecordFieldDecorator;
import com.boozallen.aiops.mda.metamodel.element.RecordField;
import com.boozallen.aiops.mda.metamodel.element.RecordFieldType;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Decorates {@link RecordField}s with Protobuf-specific generation functionality.
 */
public class ProtobufRecordField extends BaseRecordFieldDecorator {

    /**
     * Captures unique number associated with this field, which identifies the field when
     * serialized in its encapsulating binary Protobuf message.
     */
    private int fieldNumber;

    /**
     * New decorator for {@link RecordField}.
     *
     * @param recordFieldToDecorate instance to decorate
     * @param fieldNumber           unique number that should be associated with this field
     */
    public ProtobufRecordField(RecordField recordFieldToDecorate, int fieldNumber) {
        super(recordFieldToDecorate);
        this.fieldNumber = fieldNumber;
    }

    @Override
    public RecordFieldType getType() {
        return new ProtobufRecordFieldType(super.getType());
    }

    /**
     * Returns the field name in snake case (i.e. lower case with words separated with an underscore),
     * which aligns with Protobuf naming conventions for field names
     *
     * @return
     */
    public String getSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    /**
     * Gets the unique number associated with this field which is used to identify the field
     * when serialized in its encapsulating binary Protobuf message.
     *
     * @return
     */
    public int getFieldNumber() {
        return fieldNumber;
    }
}
