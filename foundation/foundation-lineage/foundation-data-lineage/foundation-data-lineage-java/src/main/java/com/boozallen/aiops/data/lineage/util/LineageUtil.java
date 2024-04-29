package com.boozallen.aiops.data.lineage.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.EventEmitter;
import com.boozallen.aiops.data.lineage.Facet;
import com.boozallen.aiops.data.lineage.InputDataset;
import com.boozallen.aiops.data.lineage.Job;
import com.boozallen.aiops.data.lineage.JobFacet;
import com.boozallen.aiops.data.lineage.OutputDataset;
import com.boozallen.aiops.data.lineage.Run;
import com.boozallen.aiops.data.lineage.RunEvent;
import com.boozallen.aiops.data.lineage.RunFacet;
import io.openlineage.client.OpenLineage;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Lineage Utility class
 */
public class LineageUtil {
    /**
     * Creates a Run object with the given run_id, facets
     * @param runId
     * @param facets
     * @param parentRunFacet OpenLineage parent run facet for the run
     * @return the Run instance created from input arguments
     */
    private static final Logger logger = LoggerFactory.getLogger(LineageUtil.class);

    public static Run createRun(UUID runId, Map<String, RunFacet> facets, OpenLineage.ParentRunFacet parentRunFacet) {
        Run run = new Run(runId, facets == null? new HashMap<>(): facets);
        if (parentRunFacet != null) {
            run.getFacets().put("parent", RunFacet.fromOpenLineage(parentRunFacet));
        }
        return run;
    }

    /**
     * Creates a Job object with given job_name and facets.
     *
     * @param jobName
     * @param facets
     * @param defaultNamespace
     * @return the Job instance created from input arguments.
     */
    public static Job createJob(String jobName, Map<String, JobFacet> facets, String defaultNamespace) {
        return new Job(jobName, facets, defaultNamespace);
    }

    /**
     * Creates the RunEvent object with give Run, Job, status (RunState/EventType), inputs and outputs dataset.
     *
     * @param run the run for the run event
     * @param job the job for the run event
     * @param status the status of the run event
     * @param inputs the input datasets for the run event
     * @param outputs the output datasets for the run event
     * @return RunEvent instance created from the input arguments
     */
    public static RunEvent createRunEvent(Run run, Job job, String status, List<InputDataset> inputs, List<OutputDataset> outputs) {
        RunEvent event = new RunEvent (run, job, status);
        if (inputs != null) {
            event.setInputs(inputs);
        }
        if (outputs != null) {
            event.setOutputs(outputs);
        }
        return event;
    }

    /**
     * Creates the Start RunEvent with given uuid, parent run facet, job name and event data
     * @param runId
     * @param jobName
     * @param defaultNamespace
     * @param parentRunFacet
     * @param eventData
     * @return RunEvent created from the input arguments
     */
    public static RunEvent createStartRunEvent(UUID runId, String jobName, String defaultNamespace, OpenLineage.ParentRunFacet parentRunFacet, LineageEventData eventData) {
        return createRunEvent(
                createRun(runId, eventData != null? eventData.getRunFacets(): null, parentRunFacet),
                createJob(jobName, eventData != null? eventData.getJobFacets(): null, defaultNamespace),
                OpenLineage.RunEvent.EventType.START.name(),
                eventData != null? eventData.getInputs(): null,
                eventData != null? eventData.getOutputs(): null
        );
    }

    /**
     * Creates the Complete RunEvent with given uuid, parent run facet, job name and event data
     * @param runId
     * @param jobName
     * @param defaultNamespace
     * @param parentRunFacet
     * @param eventData
     * @return RunEvent created from the input arguments
     */
    public static RunEvent createCompleteRunEvent(UUID runId, String jobName, String defaultNamespace, OpenLineage.ParentRunFacet parentRunFacet, LineageEventData eventData) {
        return createRunEvent(
                createRun(runId, eventData != null? eventData.getRunFacets(): null, parentRunFacet),
                createJob(jobName, eventData != null? eventData.getJobFacets(): null, defaultNamespace),
                OpenLineage.RunEvent.EventType.COMPLETE.name(),
                eventData != null? eventData.getInputs(): null,
                eventData != null? eventData.getOutputs(): null
        );
    }

    /**
     * Creates the Fail RunEvent with given uuid, parent run facet, job name and event data
     * @param runId
     * @param jobName
     * @param defaultNamespace
     * @param parentRunFacet
     * @param eventData
     * @return RunEvent created from the input arguments
     */
    public static RunEvent createFailRunEvent(UUID runId, String jobName, String defaultNamespace, OpenLineage.ParentRunFacet parentRunFacet, LineageEventData eventData) {
        return createRunEvent(
                createRun(runId, eventData != null? eventData.getRunFacets(): null, parentRunFacet),
                createJob(jobName, eventData != null? eventData.getJobFacets(): null, defaultNamespace),
                OpenLineage.RunEvent.EventType.FAIL.name(),
                eventData != null? eventData.getInputs(): null,
                eventData != null? eventData.getOutputs(): null
        );
    }

    public static void recordLineage(RunEvent event){
        logger.info("Recording lineage data...");
        EventEmitter.emitEvent(event);
        logger.info("Lineage recorded");
    }

    /**
     * LineageEventData captures the job facets, run facets, and input/output datasets that can be included in a lineage event
     */
    public static class LineageEventData {
        private List<InputDataset> inputs;
        private List<OutputDataset> outputs;
        private Map<String, RunFacet> runFacets;
        private Map<String, JobFacet> jobFacets;

        public LineageEventData() {}

        /**
         * get job facets
         * @return job facet list
         */
        public Map<String, JobFacet> getJobFacets() {
            return jobFacets;
        }

        /**
         * set job facets
         * @param jobFacets job facet list
         */
        public void setJobFacets(Map<String, JobFacet> jobFacets) {
            this.jobFacets = jobFacets;
        }

        /**
         * get run facets
         * @return run facet list
         */
        public Map<String, RunFacet> getRunFacets() {
            return runFacets;
        }

        /**
         * set run facets
         * @param runFacets set run facet list
         */
        public void setRunFacets(Map<String, RunFacet> runFacets) {
            this.runFacets = runFacets;
        }

        /**
         * get input datasets
         * @return a list of input datasets
         */
        public List<InputDataset> getInputs() {
            return inputs;
        }

        /**
         * set input datasets
         * @param inputs set input datasets
         */
        public void setInputs(List<InputDataset> inputs) {
            this.inputs = inputs;
        }

        /**
         * get output datasets
         * @return a list of output datasets
         */
        public List<OutputDataset> getOutputs() {
            return outputs;
        }

        /**
         * set output datasets
         * @param outputs set output datasets
         */
        public void setOutputs(List<OutputDataset> outputs) {
            this.outputs = outputs;
        }
    }
}
