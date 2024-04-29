package com.boozallen.aissemble.pipeline.invocation.service.endpoint;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.pipeline.invocation.service.PipelineInvocationAgent;
import com.boozallen.aissemble.pipeline.invocation.service.serialization.PipelineInvocationRequest;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Entrypoint for message-based requests for SparkApplication submission.
 */
@ApplicationScoped
public class MessageEndpoint {
    public static final String CHANNEL_NAME = "pipeline-invocation";

    private static final Logger logger = Logger.getLogger(MessageEndpoint.class);

    @Inject
    PipelineInvocationAgent pipelineInvocationAgent;

    /**
     * Receives a message requesting the submission of a SparkApplication to the cluster for processing
     * @param rawRequest: JSON string representing the desired PipelineInvocationRequest.
     */
    @Incoming(CHANNEL_NAME)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void receivePipelineRequest(String rawRequest) {
        try {
            PipelineInvocationRequest pipelineInvocationRequest = PipelineInvocationRequest.fromString(rawRequest);
            logger.info("Received message request to submit " + pipelineInvocationRequest.getApplicationName() + ".");
            pipelineInvocationAgent.submitSparkApplication(pipelineInvocationRequest);
            logger.info("Submitted " + pipelineInvocationRequest.getApplicationName() + " for processing.");
        } catch (Exception failsafe) {
            throw new RuntimeException(failsafe);
        }
    }
}
