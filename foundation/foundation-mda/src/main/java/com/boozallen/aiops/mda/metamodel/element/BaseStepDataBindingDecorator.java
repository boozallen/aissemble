package com.boozallen.aiops.mda.metamodel.element;

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

/**
 * Provides baseline decorator functionality for {@link StepDataBinding}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various implementations of generators (e.g., Java,
 * python, Docker) so that each concrete decorator only has to decorate those aspects of the class that are needed, not
 * all the pass-through methods that each decorator would otherwise need to implement (that add no real value).
 */
public class BaseStepDataBindingDecorator implements StepDataBinding {

    protected StepDataBinding wrapped;

    /**
     * New decorator for {@link StepDataBinding}.
     * 
     * @param stepDataBindingToDecorate
     *            instance to decorate
     */
    public BaseStepDataBindingDecorator(StepDataBinding stepDataBindingToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), stepDataBindingToDecorate);
        wrapped = stepDataBindingToDecorate;
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
    public StepDataCollectionType getNativeCollectionType() {
        return wrapped.getNativeCollectionType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDataRecordType getRecordType() {
        return wrapped.getRecordType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelType() {
        return wrapped.getChannelType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getChannelName() {
        return wrapped.getChannelName();
    }

}
