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

import org.apache.commons.lang3.StringUtils;

import com.boozallen.aiops.mda.metamodel.element.BaseStepDataRecordTypeDecorator;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;

/**
 * Decorates StepDataRecordType with Java-specific functionality.
 */
public class JavaStepDataRecordType extends BaseStepDataRecordTypeDecorator {

    /**
     * {@inheritDoc}
     */
    public JavaStepDataRecordType(StepDataRecordType stepDataRecordTypeToDecorate) {
        super(stepDataRecordTypeToDecorate);
    }

    /**
     * Returns the fully qualified type of this step data record type.
     * 
     * @return fully qualified type
     */
    public String getFullyQualifiedType() {
        String fullyQualifiedRecordType = null;

        String packageName = getPackage();
        if (StringUtils.isNotBlank(packageName) ){
            fullyQualifiedRecordType = packageName + "." + getName();
        }

        return fullyQualifiedRecordType;
    }
}
