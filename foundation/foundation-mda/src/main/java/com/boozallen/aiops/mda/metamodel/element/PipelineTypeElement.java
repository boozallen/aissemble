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

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelElement;

import com.boozallen.aiops.mda.generator.common.PipelineEnum;
import com.boozallen.aiops.mda.generator.common.PipelineImplementationEnum;
import com.boozallen.aiops.mda.generator.common.PipelinePlatformEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Represents a pipeline type instance.
 */
@JsonPropertyOrder({ "name", "implementation", "versioning", "platforms", "executionHelpers" })
public class PipelineTypeElement extends MetamodelElement implements PipelineType {

	private String implementation;

    @JsonInclude(Include.NON_NULL)
    private Versioning versioning;

	@JsonInclude(Include.NON_NULL)
	private String storage;

    @JsonInclude(Include.NON_NULL)
    private List<Platform> platforms;
    
    @JsonInclude(Include.NON_NULL)
    private List<String> executionHelpers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getImplementation() {
		return implementation;
	}

    /**
     * {@inheritDoc}
     */
	@JsonInclude(Include.NON_NULL)
    @Override
    public Versioning getVersioning() {
        return versioning;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Platform> getPlatforms() {
        return platforms;
    }
    
    public List<String> getExecutionHelpers() {
    	return executionHelpers;
    }

    /**
     * Sets the storage layer of this pipeline.
     *
     * @param storage
     *            type
     */
    public void setStorage(String storage) {
        this.storage = storage;
    }

	/**
	 * Sets the implementation type of this pipeline.
	 * 
	 * @param implementation type
	 */
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

    /**
     * Sets the versioning settings of this pipeline.
     * 
     * @param versioning
     *            the versioning settings
     */
    public void setVersioning(Versioning versioning) {
        this.versioning = versioning;
    }

    /**
     * Sets the platforms for this pipeline.
     * 
     * @param platforms
     */
    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }
    
    /**
     * Sets the platforms for this pipeline.
     * 
     * @param platforms
     */
    public void setExecutionHelpers(List<String> helpers) {
    	this.executionHelpers = helpers;
    }

	@Override
	public void validate() {
		super.validate();

		if (!PipelineEnum.isValidType(getName())) {
			messageTracker.addErrorMessage("A pipeline type with an unrecognized name (" + name + ") has been specified!" +
					" Must be one of " + PipelineEnum.getPossibleValues());
		}
		
		if (StringUtils.isBlank(getImplementation())) {
            messageTracker.addErrorMessage("A pipeline type has been specified without a required implementation!");
		}
		
		if (!PipelineImplementationEnum.isValidImplementation(getImplementation())) {
			messageTracker.addErrorMessage("A pipeline type has been specified that does not match any expected implementation!" +
					" Must be one of " + PipelineImplementationEnum.getPossibleValues());
		}

		if (getVersioning() != null && !PipelineEnum.MACHINE_LEARNING.equalsIgnoreCase(getName())) {
		    messageTracker.addErrorMessage("Versioning is only valid for machine-learning pipeline types!");
		}

        if (CollectionUtils.isNotEmpty(getPlatforms())) {
            for (Platform platform : getPlatforms()) {
                if (!PipelinePlatformEnum.isValidPipelinePlatform(platform.getName(), getImplementation())) {
                    messageTracker.addErrorMessage(
                            "Invalid platform specified for " + getImplementation() + " pipeline: " + platform);
                }
            }
        }
        
	}

}
