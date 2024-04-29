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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.boozallen.aiops.mda.generator.common.PipelineExecuterHelperEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.NamespacedMetamodelElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a pipeline instance.
 */
@JsonPropertyOrder({"package", "name", "description", "type"})
public class PipelineElement extends NamespacedMetamodelElement implements Pipeline {

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonProperty("type")
    private PipelineType type;

    @JsonInclude(Include.NON_NULL)
    private List<FileStore> fileStores = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    private boolean dataLineage;

    @JsonInclude(Include.NON_NULL)
    private List<Step> steps = new ArrayList<>();

    @JsonInclude(Include.NON_NULL)
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @JsonProperty("type")
    @Override
    public PipelineType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<FileStore> getFileStores() {
        return fileStores;
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public List<Step> getSteps() {
        return steps;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public boolean isDataProfilingEnabled() {
        boolean result = false;

        if (steps != null) {
            for (Step step : steps) {
                if (step.isDataProfilingEnabled()) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAirflow() {
        return getType().getExecutionHelpers() != null && getType().getExecutionHelpers().contains(PipelineExecuterHelperEnum.AIRFLOW.getExecutionHelperType());
    }

    /**
     * {@inheritDoc}
     */
    @JsonInclude(Include.NON_NULL)
    @Override
    public boolean getDataLineage() {
        return dataLineage;
    }

    public void setDataLineage(boolean enabled) {
        this.dataLineage = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSchemaFileName() {
        return "aiops-pipeline-schema.json" ;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(PipelineType type) {
        this.type = type;
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    @Override
    public void validate() {
        super.validate();

        type.validate();

        Set<String> validFileStoreNames = new HashSet<>();
        for (FileStore fileStore : fileStores) {
            fileStore.validate();
            validFileStoreNames.add(fileStore.getName());
        }
        boolean isMlPipeline = this.getType().getName().equals("machine-learning");
        if (isMlPipeline && getDataLineage()) {
            messageTracker.addErrorMessage("Pipeline [" + getName() + "] cannot have data lineage because it is a machine-learning pipeline");
        }

        for (Step step : steps) {
            step.validate();

            if (isMlPipeline) {
                validateMlStep(step);
            } else {
                validateDataStep(step);
            }
            //steps don't know what file stores are valid, so check here
            if (step.getFileStores() != null) {
                String invalidStores = step.getFileStores()
                        .stream()
                        .filter(store -> !validFileStoreNames.contains(store))
                        .collect(Collectors.joining(", "));
                if (StringUtils.isNotBlank(invalidStores)) {
                    messageTracker.addErrorMessage("Step [" + step.getName() + "] contains invalid file store references: " + invalidStores);
                }
            }
        }
    }

    private void validateDataStep(Step step) {
        if (step.getModelLineage() != null) {
            messageTracker.addErrorMessage("Pipeline [" + getName() + "] has invalid step [" + step.getName() + "] because data-flow pipelines do not support model lineage");
        }
    }

    private void validateMlStep(Step step) {
        if (step.getDataProfiling() != null) {
            messageTracker.addErrorMessage("Pipeline [" + getName() + "] cannot have data profiling because it is a machine-learning pipeline");
        }
    }

}
