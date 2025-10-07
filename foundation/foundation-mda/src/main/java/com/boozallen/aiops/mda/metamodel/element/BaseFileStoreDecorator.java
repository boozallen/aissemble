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
import org.codehaus.plexus.util.StringUtils;

public class BaseFileStoreDecorator implements FileStore {

    protected FileStore wrapped;

    /**
     * New decorator for {@link FileStore}.
     *
     * @param fileStoreToDecorate instance to decorate
     */
    public BaseFileStoreDecorator(FileStore fileStoreToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), fileStoreToDecorate);
        wrapped = fileStoreToDecorate;
    }

    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public void validate() {
        wrapped.validate();
    }

    public String getFullName() {
        return StringUtils.capitalise(wrapped.getName()) + "Store";
    }

    public String getLowerName() {
        return StringUtils.lowercaseFirstLetter(getFullName());
    }
}
