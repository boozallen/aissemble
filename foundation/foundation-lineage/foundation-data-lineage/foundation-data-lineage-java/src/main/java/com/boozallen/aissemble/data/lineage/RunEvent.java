package com.boozallen.aissemble.data.lineage;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.config.ConfigUtil;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineage.RunEvent.EventType;

import java.net.URI;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The root level object in the structure of a Data Lineage event. In addition to the properties of this event,
 * it also contains all other objects that make up the event's contents.
 */
public class RunEvent {

    private String eventType;
    private Date eventTime;
    private Run run;
    private Job job;
    private String producer;
    private String schemaURL;
    private List<InputDataset> inputs;
    private List<OutputDataset> outputs;
    private static ConfigUtil util = ConfigUtil.getInstance();

    public RunEvent(Run run, Job job, String eventType) {
        this.schemaURL = util.getDataLineageSchemaUrl();
        this.eventTime = new Date();
        this.producer = util.getProducer(job.getName());
        this.run = run;
        this.job = job;
        this.eventType = eventType;
    }

    /**
     * Returns a RunEvent object from the OpenLineage Client library
     * @return A RunEvent
     */
    public OpenLineage.RunEvent getOpenLineageRunEvent() {
        URI producer = URI.create(this.producer);
        OpenLineage openLineage = new OpenLineage(producer);
        ZonedDateTime zTime = this.eventTime.toInstant().atZone(ZoneId.of("UTC"));

        ArrayList<OpenLineage.InputDataset> olInputs = new ArrayList<>();
        if(inputs != null) {
            inputs.forEach(input -> {
                olInputs.add((OpenLineage.InputDataset) input.getOpenLineageDataset());
            });
        }

        ArrayList<OpenLineage.OutputDataset> olOutputs = new ArrayList<>();
        if(outputs != null) {
            outputs.forEach(output -> {
                olOutputs.add((OpenLineage.OutputDataset) output.getOpenLineageDataset());
            });
        }

        return openLineage.newRunEvent(EventType.valueOf(this.eventType),
                zTime,
                run.getOpenLineageRun(),
                job.getOpenLineageJob(),
                !olInputs.isEmpty() ? olInputs : null,
                !olOutputs.isEmpty() ? olOutputs : null);
    }

    /**
     * Accessor for the event type field
     * @return This RunEvent's type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the event type field value
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Accessor for the event time field
     * @return This RunEvent's type
     */
    public Date getEventTime() {
        return eventTime;
    }

    /**
     * Sets the event time value
     */
    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Accessor for the contained Run object
     * @return The Run contained in this RunEvent
     */
    public Run getRun() {
        return run;
    }

    /**
     * Sets the contained Run to a given Run object
     * @param run The new Run for this RunEvent
     */
    public void setRun(Run run) {
        this.run = run;
    }

    /**
     * Accessor for the contained Job object
     * @return The Job contained in this RunEvent
     */
    public Job getJob() {
        return job;
    }

    /**
     * Sets the contained Job to a given Job object
     * @param job The new Job for this RunEvent
     */
    public void setJob(Job job) {
        this.job = job;
    }

    /**
     * Accessor for the producer field
     * @return This RunEvent's producer
     */
    public String getProducer() {
        return producer;
    }

    /**
     * Sets the producer value
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * Accessor for the schema used to define this RunEvent's structure
     * @return This RunEvent's schema
     */
    public String getSchemaURL() {
        return schemaURL;
    }

    /**
     * Sets the schema value
     */
    public void setSchemaURL(String schemaURL) {
        this.schemaURL = schemaURL;
    }

    /**
     * Accessor for the inputs Datasets
     * @return The inputs list
     */
    public List<InputDataset> getInputs() {
        return inputs;
    }

    /**
     * Sets the inputs datasets to a given List of values
     * @param inputs The List of inputs for this RunEvent
     */
    public void setInputs(List<InputDataset> inputs) {
        this.inputs = inputs;
    }

    /**
     * Accessor for the outputs Datasets
     * @return The outputs list
     */
    public List<OutputDataset> getOutputs() {
        return outputs;
    }

    /**
     * Sets the outputs datasets to a given List of values
     * @param outputs The List of outputs for this RunEvent
     */
    public void setOutputs(List<OutputDataset> outputs) {
        this.outputs = outputs;
    }

}
