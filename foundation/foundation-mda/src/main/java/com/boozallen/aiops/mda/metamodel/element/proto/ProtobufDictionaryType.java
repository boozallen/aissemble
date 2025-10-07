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

import com.boozallen.aiops.mda.metamodel.element.BaseDictionaryTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.DictionaryType;

/**
 * Decorates {@link DictionaryType}s with Protobuf-specific generation functionality.
 */
public class ProtobufDictionaryType extends BaseDictionaryTypeDecorator {

    /**
     * New decorator for {@link DictionaryType}.
     *
     * @param dictionaryTypeToDecorate instance to decorate
     */
    public ProtobufDictionaryType(DictionaryType dictionaryTypeToDecorate) {
        super(dictionaryTypeToDecorate);
    }

    /**
     * Appends "-proto" to the provided simple type in order for multiple simple types to be
     * associated with their corresponding language-specific type defined in types.json. For example,
     * by specifying a dictionary simple type as "string", it may be automatically used for Python
     * generation as "string-python" and Protobuf generation as "string-proto".
     *
     * @return
     */
    @Override
    public String getSimpleType() {
        return super.getSimpleType() + "-proto";
    }

}
