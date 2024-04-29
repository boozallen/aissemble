package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
