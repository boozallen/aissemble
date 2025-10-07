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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.maven.shared.utils.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import com.boozallen.aiops.mda.generator.common.PersistType;
import com.boozallen.aiops.mda.generator.common.PipelineExecuterHelperEnum;
import com.boozallen.aiops.mda.generator.common.PipelinePlatformEnum;
import com.boozallen.aiops.mda.generator.util.PipelineUtils;

/**
 * Provides baseline decorator functionality for {@link Pipeline}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various
 * implementations of generators (e.g., Java, python, Docker) so that each
 * concrete decorator only has to decorate those aspects of the class that are
 * needed, not all the pass-through methods that each decorator would otherwise
 * need to implement (that add no real value).
 */
public class BasePipelineDecorator implements Pipeline  {

	protected Pipeline wrapped;

	/**
	 * New decorator for {@link Pipeline}.
	 * 
	 * @param pipelineToDecorate instance to decorate
	 */
	public BasePipelineDecorator(Pipeline pipelineToDecorate) {
		MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), pipelineToDecorate);
		wrapped = pipelineToDecorate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return wrapped.getName();
	}

	/**
	 * Converts the pipeline name to artifact id
	 * @return
	 */
	public String deriveArtifactIdFromCamelCase() {
		return PipelineUtils.deriveArtifactIdFromCamelCase(wrapped.getName());
	}

	/**
	 * Returns the pipeline name formatted into lowercase with underscores
	 * (Python naming convention).
	 *
	 * @return the pipeline name formatted into lowercase with underscores
	 */
	public String deriveLowercaseSnakeCaseNameFromCamelCase() {
		return PipelineUtils.deriveLowercaseSnakeCaseNameFromCamelCase(wrapped.getName());
	}

	@Override
	public String getFileName() {
		return wrapped.getFileName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPackage() {
		return wrapped.getPackage();
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
	public String getDescription() {
		return wrapped.getDescription();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PipelineType getType() {
		return new BasePipelineTypeDecorator(wrapped.getType());
	}

	/**
	 * Returns the file stores used by this pipeline.
	 *
	 * @return file stores
	 */
	@Override
	public List<FileStore> getFileStores() {
		List<FileStore> wrappedFileStores = new ArrayList<>();
		for (FileStore fileStore : wrapped.getFileStores()) {
			BaseFileStoreDecorator baseFileStoreDecorator = new BaseFileStoreDecorator(fileStore);
			wrappedFileStores.add(baseFileStoreDecorator);
		}
		return wrappedFileStores;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BaseStepDecorator> getSteps() {
		List<BaseStepDecorator> wrappedSteps = new ArrayList<>();
		for (Step step : wrapped.getSteps()) {
			BaseStepDecorator wrappedField = new BaseStepDecorator(step);
			wrappedSteps.add(wrappedField);
		}

		return wrappedSteps;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDataProfilingEnabled() {
		return wrapped.isDataProfilingEnabled();
	}

	/**
	 * Returns the capitalized name of the pipeline.
	 * 
	 * @return capitalized name
	 */
	public String getCapitalizedName() {
		return StringUtils.capitalise(getName());
	}


	/**
	 * @return true if any step uses `messaging` or `multimessaging` type inbound/outbound
 	 */
	public boolean hasMessagingSteps() {
		for (BaseStepDecorator step : getSteps()) {
			if(step.hasMessagingInbound() || step.hasMessagingOutbound()) {
				return true;
			}
		}
		return false;
	}

    /**
     * Whether or not the pipeline has an asynchronous step.
     * 
     * @return true if the pipeline has an asynchronous step.
     */
    public boolean hasAsynchronousStep() {
        boolean hasAsynchronousStep = false;
        for (Step step : getSteps()) {
            BaseStepDecorator stepDecorator = (BaseStepDecorator) step;
            if (stepDecorator.isAsynchronous()) {
                hasAsynchronousStep = true;
                break;
            }
        }

        return hasAsynchronousStep;
    }

	/**
	 * Whether or not the pipeline has a messaging inbound step.
	 *
	 * @return true if the pipeline has a messaging inbound step.
	 */
	public boolean hasMessagingInboundStep() {
		boolean hasMessagingInboundStep = false;
		for (Step step : getSteps()) {
			BaseStepDecorator stepDecorator = (BaseStepDecorator) step;
			if (stepDecorator.hasMessagingInbound()) {
				hasMessagingInboundStep = true;
				break;
			}
		}

		return hasMessagingInboundStep;
	}

    /**
     * Returns the first step in the pipeline with the given step type.
     * 
     * @param type
     *            the type of the step
     * @return the step with the given type
     */
    public Step getStepByType(String type) {
        Step stepByType = null;
        for (Step step : getSteps()) {
            if (type.equals(step.getType())) {
                stepByType = step;
                break;
            }
        }

        return stepByType;
    }

    /**
     * Whether hive support is needed for this pipeline.
     * 
     * @return true if hive support is needed
     */
    public boolean isHiveSupportNeeded() {
        boolean persistTypeHive = hasPersistType(PersistType.HIVE);
        boolean isMetadataEnabled = isMetadataNeeded();

        return isMetadataEnabled || persistTypeHive;
    }

    /**
     * Whether sedona support is needed for this pipeline.
     * 
     * @return true if sedona support is needed
     */
    public boolean isSedonaSupportNeeded() {
        return hasPlatform(PipelinePlatformEnum.SEDONA);
    }

    /**
     * Whether delta support is needed for this pipeline.
     * 
     * @return true if delta support is needed
     */
    public boolean isDeltaSupportNeeded() {
        return hasPersistType(PersistType.DELTA_LAKE);
    }

    /**
     * Whether postgres support is needed for this pipeline.
     * 
     * @return true if postgres support is needed
     */
    public boolean isPostgresSupportNeeded() {
        return hasPersistType(PersistType.POSTGRES);
    }

    /**
     * Whether rdbms support is needed for this pipeline.
     * 
     * @return true if rdbms support is needed
     */
    public boolean isRdbmsSupportNeeded() {
        return hasPersistType(PersistType.RDBMS);
    }


    /**
     * Whether elasticsearch support is needed for this pipeline.
     * 
     * @return true if elasticsearch support is needed
     */
    public boolean isElasticsearchSupportNeeded() {
        return hasPersistType(PersistType.ELASTICSEARCH);
    }

    /**
     * Whether neo4j support is needed for this pipeline.
     * 
     * @return true if neo4j support is needed
     */
    public boolean isNeo4jSupportNeeded() {
        return hasPersistType(PersistType.NEO4J);
    }

	/**
	 * Whether alerting is needed for this pipeline.
	 *
	 * @return true if alerting is needed
	 */
	public boolean isAlertingSupportNeeded() {
		return getSteps()
				.stream()
				.anyMatch(BaseStepDecorator::isAlertingEnabled);
	}

    /**
     * Whether data profiling support is needed for this pipeline.
     * 
     * @return true if data profiling support is needed
     */
    public boolean isDataProfilingSupportNeeded() {
        boolean enableDataProfilingSupport = false;
        for (Step step : getSteps()) {
            // if the pipeline has a step that has not explicitly disabled data
            // profiling, then data profiling support is needed
            if (step.getDataProfiling() == null || step.getDataProfiling().isEnabled()) {
                enableDataProfilingSupport = true;
                break;
            }
        }

        return enableDataProfilingSupport;
    }

	/**
	 * @return true iff any step in the pipeline has model lineage enabled
	 */
	public boolean isModelLineageSupportNeeded() {
		return getSteps().stream().anyMatch(step -> step.isModelLineageEnabled());
	}

    /**
     * Whether metadata is needed for this pipeline.
     * 
     * @return true if metadata is needed
     */
    public boolean isMetadataNeeded() {
        return getSteps()
                .stream()
                .anyMatch(BaseStepDecorator::isMetadataEnabled);
    }

	/**
	 * @return true if any features that require messaging are enabled
	 */
	public boolean isMessagingSupportNeeded() {
		return isAlertingSupportNeeded() ||
				isMetadataNeeded() ||
				hasMessagingSteps();
	}

    private boolean hasPersistType(PersistType persistType) {
        boolean hasPersistType = false;

        if (persistType != null) {
            // if any step has a persist type for which we are searching, it is turned on
            for (Step step : getSteps()) {
                if (PersistType.hasPersistType(step.getPersist(), persistType)) {
                    hasPersistType = true;
                    break;
                }
            }

        } else {
            throw new GenerationException("A valid persistType is required!");

        }

        return hasPersistType;
    }

    private boolean hasPlatform(PipelinePlatformEnum platform) {
        boolean hasPlatform = false;

        List<Platform> platforms = getType().getPlatforms();
        if (CollectionUtils.isNotEmpty(platforms)) {
            List<String> platformNames = platforms.stream()
                    .map(Platform::getName)
                    .collect(Collectors.toList());

            hasPlatform = platformNames.contains(platform.getPlatformName());
        }

        return hasPlatform;
    }

	/**
	 * {@inheritDoc}
	 */
	public boolean requiresAirflow() {
		return getType().getExecutionHelpers()!=null && getType().getExecutionHelpers().contains(PipelineExecuterHelperEnum.AIRFLOW.getExecutionHelperType());
	}

	@Override
	public boolean getDataLineage() {
		return wrapped.getDataLineage();
	}
}
