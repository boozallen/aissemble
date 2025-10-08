package com.boozallen.aiops.mda.metamodel.element;

import org.apache.commons.lang3.StringUtils;

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

import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import com.boozallen.aiops.mda.generator.common.PersistMode;

/**
 * Provides baseline decorator functionality for {@link Persist}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BasePersistDecorator implements Persist {

    protected Persist wrapped;

    /**
     * New decorator for {@link Persist}.
     * 
     * @param persistToDecorate
     *            instance to decorate
     */
    public BasePersistDecorator(Persist persistToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), persistToDecorate);
        wrapped = persistToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        wrapped.validate();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return wrapped.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMode() {
        String mode = wrapped.getMode();

        if (StringUtils.isBlank(mode)) {
            mode = PersistMode.APPEND.getModeType();
        }

        return mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataCollectionType getCollectionType() {
        return wrapped.getCollectionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return wrapped.getRecordType();
    }

}
