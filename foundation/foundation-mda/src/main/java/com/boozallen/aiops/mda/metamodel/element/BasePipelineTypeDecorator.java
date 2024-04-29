package com.boozallen.aiops.mda.metamodel.element;

import java.util.List;

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

/**
 * Provides baseline decorator functionality for {@link PipelineType}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various
 * implementations of generators (e.g., Java, python, Docker) so that each
 * concrete decorator only has to decorate those aspects of the class that are
 * needed, not all the pass-through methods that each decorator would otherwise
 * need to implement (that add no real value).
 */
public class BasePipelineTypeDecorator implements PipelineType {

	protected PipelineType wrapped;

	/**
	 * New decorator for {@link PipelineType}.
	 * 
	 * @param pipelineTypeToDecorate instance to decorate
	 */
	public BasePipelineTypeDecorator(PipelineType pipelineTypeToDecorate) {
		MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), pipelineTypeToDecorate);
		wrapped = pipelineTypeToDecorate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileName() {
		return wrapped.getFileName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return wrapped.getName();
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
	public String getImplementation() {
		return wrapped.getImplementation();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Versioning getVersioning() {
        return wrapped.getVersioning();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Platform> getPlatforms() {
        return wrapped.getPlatforms();
    }

	@Override
	public List<String> getExecutionHelpers() {
		return wrapped.getExecutionHelpers();
	}

}
