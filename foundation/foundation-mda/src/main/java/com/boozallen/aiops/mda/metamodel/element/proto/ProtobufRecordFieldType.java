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
