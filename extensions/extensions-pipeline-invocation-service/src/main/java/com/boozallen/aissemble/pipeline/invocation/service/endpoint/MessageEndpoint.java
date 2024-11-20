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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import io.quarkus.runtime.Shutdown;

/**
 * Entrypoint for message-based requests for SparkApplication submission.
 */
@ApplicationScoped
public class MessageEndpoint {
    public static final String CHANNEL_NAME = "pipeline-invocation";

    private static final Logger logger = Logger.getLogger(MessageEndpoint.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(16);

    @Inject
    PipelineInvocationAgent pipelineInvocationAgent;

    /**
     * Receives a message requesting the submission of a SparkApplication to the cluster for processing
     * @param rawRequest: JSON string representing the desired PipelineInvocationRequest.
     */
    @Incoming(CHANNEL_NAME)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void receivePipelineRequest(String rawRequest) {
        // Running the helm command asynchronously in a separated thread to avoid vertx event loop thread block issue
        executorService.execute(() -> {
            try {
                PipelineInvocationRequest pipelineInvocationRequest = PipelineInvocationRequest.fromString(rawRequest);
                logger.info("Received message request to submit " + pipelineInvocationRequest.getApplicationName() + ".");
                pipelineInvocationAgent.submitSparkApplication(pipelineInvocationRequest);
                logger.info("Submitted " + pipelineInvocationRequest.getApplicationName() + " for processing.");
            } catch (Exception e) {
                throw new RuntimeException("Fail executing pipeline invocation command", e);
            }
        });
    }

    @Shutdown
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1500, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
