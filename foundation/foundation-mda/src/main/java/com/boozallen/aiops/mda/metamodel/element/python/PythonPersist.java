package com.boozallen.aiops.mda.metamodel.element.python;

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

import org.technologybrewery.fermenter.mda.TypeManager;

import com.boozallen.aiops.mda.metamodel.element.BasePersistDecorator;
import com.boozallen.aiops.mda.metamodel.element.Persist;
import com.boozallen.aiops.mda.metamodel.element.StepDataCollectionType;
import com.boozallen.aiops.mda.metamodel.element.StepDataRecordType;

/**
 * Decorates Persist with Python-specific functionality.
 */
public class PythonPersist extends BasePersistDecorator {

    public PythonPersist(Persist persistToDecorate) {
        super(persistToDecorate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataCollectionType getCollectionType() {
        return super.getCollectionType() != null ? new PythonStepDataCollectionType(super.getCollectionType()) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return super.getRecordType() != null ? new PythonStepDataRecordType(super.getRecordType()) : null;
    }

    /**
     * Returns the short collection type.
     * 
     * @return short collection type
     */
    public String getShortCollectionType() {
        PythonStepDataCollectionType collectionType = (PythonStepDataCollectionType) getCollectionType();
        return collectionType != null ? collectionType.getShortType() : null;
    }

    /**
     * Returns the short record type.
     * 
     * @return short record type
     */
    public String getShortRecordType() {
        PythonStepDataRecordType recordType = (PythonStepDataRecordType) getRecordType();
        return recordType != null ? recordType.getName() : TypeManager.getShortType(PythonStep.DATAFRAME_TYPE);
    }

    /**
     * Returns the fully qualified collection type.
     * 
     * @return fully qualified collection type
     */
    public String getFullyQualifiedCollectionType() {
        PythonStepDataCollectionType collectionType = (PythonStepDataCollectionType) getCollectionType();
        return collectionType != null ? collectionType.getFullyQualifiedType() : null;
    }

    /**
     * Returns the fully qualified record type.
     * 
     * @return fully qualified record type
     */
    public String getFullyQualifiedRecordType() {
        PythonStepDataRecordType recordType = (PythonStepDataRecordType) getRecordType();
        return recordType != null ? recordType.getFullyQualifiedType() : TypeManager.getFullyQualifiedType(PythonStep.DATAFRAME_TYPE);
    }

}
