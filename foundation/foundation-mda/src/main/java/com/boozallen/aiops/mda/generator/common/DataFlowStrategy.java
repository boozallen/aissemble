package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.generator.util.PipelineUtils;
import com.boozallen.aiops.mda.metamodel.element.BasePipelineDecorator;
import com.boozallen.aiops.mda.metamodel.element.Pipeline;
import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.python.PythonPipeline;

import java.util.ArrayList;
import java.util.List;

public class DataFlowStrategy extends AbstractStrategy {

    public DataFlowStrategy(List<Pipeline> pipelines) {
        super(pipelines, PipelineEnum.DATA_FLOW);
    }

    /**
     * This method gets the steps across all data-flow pipelines.
     * 
     * @return steps with their associated pipeline
     */
    @Override
    public List<PipelineStepPair> getSteps() {
        List<PipelineStepPair> steps = new ArrayList<>();

        for (Pipeline pipeline : pipelines) {
            PythonPipeline pythonPipeline = new PythonPipeline(pipeline);
            for (Step step : pythonPipeline.getSteps()) {
                steps.add(new PipelineStepPair(pipeline, step));
            }
        }

        return steps;
    }

    /**
     * Returns all pipelines with implementation type 'data-delivery-spark'.
     * 
     * @return all pipelines with implementation type 'data-delivery-spark'
     */
    public List<String> getSparkPipelines() {
        return getPipelinesByImplementation(PipelineImplementationEnum.DATA_DELIVERY_SPARK);
    }

    public boolean hasSparkPipelines() {
        return !getSparkPipelines().isEmpty();
    }

    /**
     * Returns all pipelines with implementation type 'data-delivery-pyspark'.
     * 
     * @return all pipelines with implementation type 'data-delivery-pyspark'
     */
    public List<String> getPySparkPipelines() {
        return getPipelinesByImplementation(PipelineImplementationEnum.DATA_DELIVERY_PYSPARK);
    }

    public boolean hasPySparkPipelines() {
        return !getPySparkPipelines().isEmpty();
    }

    private List<String> getPipelinesByImplementation(PipelineImplementationEnum implementation) {
        List<String> pipelinesByImplementation = new ArrayList<>();
        for (Pipeline pipeline : pipelines) {
            if (implementation.equalsIgnoreCase(pipeline.getType().getImplementation())) {
                String artifactId = PipelineUtils.deriveArtifactIdFromCamelCase(pipeline.getName());
                pipelinesByImplementation.add(artifactId);
            }
        }

        return pipelinesByImplementation;
    }

    /**
     * Checks whether there is a pipeline with a data-delivery-pyspark type
     * implementation.
     * 
     * @return true if a pipeline with a data-delivery-pyspark type
     *         implementation is found
     */
    public boolean isPySparkSupportNeeded() {
        return !getPySparkPipelines().isEmpty();
    }

    /**
     * Checks whether there is a pyspark pipeline with persist type delta-lake.
     * 
     * @return true if a pyspark pipeline with persist type delta-lake is found
     */
    public boolean isDeltaSupportNeeded() {
        boolean deltaSupportNeeded = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isDeltaSupportNeeded()) {
                deltaSupportNeeded = true;
                break;
            }
        }

        return deltaSupportNeeded;
    }

    /**
     * Checks whether there is a pipeline with persist type hive.
     * 
     * @return true if a pipeline with persist type hive is found
     */
    public boolean isHiveSupportNeeded() {
        boolean hiveSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isHiveSupportNeeded()) {
                hiveSupport = true;
                break;
            }
        }

        return hiveSupport;
    }

    /**
     * Checks whether there is a pipeline with persist type postgres.
     * 
     * @return true if a pipeline with persist type postgres is found
     */
    public boolean isPostgresSupportNeeded() {
        boolean postgresSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isPostgresSupportNeeded()) {
                postgresSupport = true;
                break;
            }
        }

        return postgresSupport;
    }

    /**
     * Checks whether there is a pipeline with persist type rdbms.
     * 
     * @return true if a pipeline with persist type rdbms is found
     */
    public boolean isRdbmsSupportNeeded() {
        boolean postgresSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isRdbmsSupportNeeded()) {
                postgresSupport = true;
                break;
            }
        }

        return postgresSupport;
    }


    /**
     * Checks whether there is a pipeline with persist type elasticsearch.
     * 
     * @return true if a pipeline with persist type elasticsearch is found
     */
    public boolean isElasticsearchSupportNeeded() {
        boolean elasticsearchSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isElasticsearchSupportNeeded()) {
                elasticsearchSupport = true;
                break;
            }
        }

        return elasticsearchSupport;
    }

    /**
     * Checks whether there is a pipeline with persist type neo4j.
     * 
     * @return true if a pipeline with persist type neo4j is found
     */
    public boolean isNeo4jSupportNeeded() {
        boolean neo4jSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isNeo4jSupportNeeded()) {
                neo4jSupport = true;
                break;
            }
        }

        return neo4jSupport;
    }

    /**
     * Checks whether there is a pipeline that has sedona added.
     * 
     * @return true if a pipeline with sedona added is found
     */
    public boolean isSedonaSupportNeeded() {
        boolean sedonaSupport = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isSedonaSupportNeeded()) {
                sedonaSupport = true;
                break;
            }
        }

        return sedonaSupport;
    }

    /**
     * Checks whether there is a pipeline with metadata enabled.
     * 
     * @return true if a pipeline with metadata enabled is found
     */
    public boolean isMetadataNeeded() {
        boolean metadataNeeded = false;

        for (Pipeline pipeline : pipelines) {
            BasePipelineDecorator pipelineDecorator = new BasePipelineDecorator(pipeline);
            if (pipelineDecorator.isMetadataNeeded()) {
                metadataNeeded = true;
                break;
            }
        }

        return metadataNeeded;
    }

    /**
     * Whether the OpenLineage client is needed for this pipeline.
     *
     * @return true if the client is needed
     */
    public boolean isDataLineageNeeded() {
        return Pipeline.aPipelineExistsWhere(pipelines, Pipeline::getDataLineage);
    }

    /**
     * Returns all the Data Delivery pipelines using Airflow as the execution helper
     * @return List of {@link BasePipelineDecorator} instantiation of the pipelines
     */
    public List<BasePipelineDecorator> getDataFlowPipelinesRequiringAirflow() {
        List<BasePipelineDecorator> decoratedPipelines = new ArrayList<BasePipelineDecorator>();

        for (Pipeline pipeline : PipelineUtils.getDataFlowPipelinesRequiringAirflow(pipelines)) {
            BasePipelineDecorator decoratedPipeline = new BasePipelineDecorator(pipeline);
            decoratedPipelines.add(decoratedPipeline);
        }
    	return decoratedPipelines;
    }
    
	/**
	 * {@inheritDoc}
	 */
	public boolean isAirflowNeeded() {
		return (getDataFlowPipelinesRequiringAirflow()!=null && getDataFlowPipelinesRequiringAirflow().size()>0);
	}

}
