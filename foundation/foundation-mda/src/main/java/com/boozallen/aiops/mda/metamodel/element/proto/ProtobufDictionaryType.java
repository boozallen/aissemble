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
