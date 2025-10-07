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

import com.boozallen.aiops.mda.ManualActionNotificationService;
import com.boozallen.aiops.mda.metamodel.element.python.MachineLearningPipeline;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.ConfigurationItem;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pipeline type instance.
 */
@JsonPropertyOrder({ "name", "type", "inbound", "outbound", "persist", "provenance", "featureDiscovery", "alerting", "modelLineage", "dataProfiling", "postActions" })
public class StepElement extends MetamodelElement implements Step {

	@JsonIgnore
	private ManualActionNotificationService manualActionNotificationService;

	private String type;

	@JsonInclude(Include.NON_NULL)
	public Object featureDiscovery;

	@JsonInclude(Include.NON_NULL)
	private StepDataBinding inbound;

	@JsonInclude(Include.NON_NULL)
	private StepDataBinding outbound;

	@JsonInclude(Include.NON_NULL)
	private Persist persist;

	@JsonInclude(Include.NON_NULL)
	private Provenance provenance;
	
	@JsonInclude(Include.NON_NULL)
	private Alerting alerting;

    @JsonInclude(Include.NON_NULL)
    private DataProfiling dataProfiling;

    @JsonInclude(Include.NON_NULL)
    private List<PostAction> postActions;

	@JsonInclude(Include.NON_NULL)
	private List<ConfigurationItem> configuration;

	@JsonInclude(Include.NON_NULL)
	private List<String> fileStores;

	@JsonInclude(Include.NON_NULL)
	private ModelLineage modelLineage;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public StepDataBinding getInbound() {
		return inbound;
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public StepDataBinding getOutbound() {
		return outbound;
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public Persist getPersist() {
		return persist;
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public Provenance getProvenance() {
		return provenance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public Alerting getAlerting() {
		return alerting;
	}

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public DataProfiling getDataProfiling() {
        return dataProfiling;
    }

	/**
	 * {@inheritDoc}
	 */

	@JsonIgnore
	@Override
	public boolean isDataProfilingEnabled() {
		return dataProfiling.isEnabled();
	}

	/**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public ModelLineage getModelLineage() {
        return modelLineage;
    }

	/**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<PostAction> getPostActions() {
        return postActions;
    }

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public List<ConfigurationItem> getConfiguration() {
		return configuration;
	}

	/**
	 * {@inheritDoc}
	 */
	@JsonInclude(Include.NON_NULL)
	@Override
	public List<String> getFileStores() {
		return fileStores;
	}

	/**
	 * Sets the file stores for this step by name
	 *
	 * @param fileStores - a list of file store names
	 */
	public void setFileStores(List<String> fileStores) {
		this.fileStores = fileStores;
	}
	
	/**
	 * Sets the alerting enabled for this step.
	 * @param alerting
	 */
	public void setAlerting(Alerting alerting) {
		this.alerting = alerting;
	}

    /**
     * Sets the data profiling settings for this step.
     * 
     * @param dataProfiling
     */
    public void setDataProfiling(DataProfiling dataProfiling) {
        this.dataProfiling = dataProfiling;
    }

	/**
	 * Sets the type of this pipeline.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Sets the inbound data binding of this step.
	 * 
	 * @param inbound inbound data binding
	 */
	public void setInbound(StepDataBinding inbound) {
		this.inbound = inbound;
	}

	/**
	 * Sets the outbound data binding of this step.
	 * 
	 * @param outbound outbound data binding
	 */
	public void setOutbound(StepDataBinding outbound) {
		this.outbound = outbound;
	}

	/**
	 * Sets the persist settings for this step.
	 * 
	 * @param persist persist settings
	 */
	public void setPersist(Persist persist) {
		this.persist = persist;
	}

	/**
	 * Sets the provenance settings for this step.
	 * @param provenance provenance settings
	 */
	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

	/**
	 * Sets the model lineage settings for this step.
	 * @param ModelLineage modelLineage settings
	 */
	public void setModelLineage(ModelLineage modelLineage) {
		this.modelLineage = modelLineage;
	}

    /**
     * Adds a post-action for this step.
     * 
     * @param postAction
     *            the post-action to add
     */
    public void addPostAction(PostAction postAction) {
        if (postActions == null) {
            postActions = new ArrayList<>();
        }

        postActions.add(postAction);
    }

    /**
     * Adds a configuration item for this step.
     * 
     * @param configurationItem
     *            the configuration item to add
     */
	public void addConfigurationItem(ConfigurationItem configurationItem) {
		if (configuration == null) {
			configuration = new ArrayList<>();

		} 
			
		configuration.add(configurationItem);

	}

	@Override
	public void validate() {
		super.validate();

		if (StringUtils.isBlank(getType())) {
			messageTracker.addErrorMessage("A pipeline type has been specified without a required type!");

		}

		if (inbound != null) {
			inbound.validate();
		}

		if (outbound != null) {
			outbound.validate();
		}

		if (persist != null) {
			persist.validate();
		}

		if (featureDiscovery != null) {
			manualActionNotificationService.addSchemaElementDeprecationNotice("featureDiscovery", "Pipeline/step");
		}

		if (getConfiguration() != null) {
			for (ConfigurationItem configurationItem : getConfiguration()) {
				configurationItem.validate();
			}
		}

        if (getPostActions() != null) {
            if (MachineLearningPipeline.TRAINING_STEP_TYPE.equals(getType())) {
                for (PostAction postAction : getPostActions()) {
                    postAction.validate();
                }
            } else {
                messageTracker.addWarningMessage("Post actions found on non-training step " + getName()
                        + " - post actions will be ignored for this step!");
            }
        }
	}

}
