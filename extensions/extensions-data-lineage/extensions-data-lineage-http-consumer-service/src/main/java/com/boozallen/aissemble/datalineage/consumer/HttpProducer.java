package com.boozallen.aissemble.datalineage.consumer;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Lineage::Http Consumer Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

/**
 * Helper bean for interacting with the HTTP client.  Wraps posts with smallrye fault tolerance.
 */

@ApplicationScoped
public class HttpProducer {
    @RestClient
    HttpProducerService httpProducerService;

    /**
     * Submission helper for publishing content to the HTTP endpoint.
     *
     * @param runEvent: String content to be published via POST to the endpoint.
     *
     * @return: A CompletionStage<String> representing the asynchronous execution and response of the HTTP POST
     */
    @Retry(maxRetries = 0, delayUnit = ChronoUnit.SECONDS)
    @Timeout
    @POST
    public CompletionStage<String> postEventHttp(String runEvent) {
        return getClient().postEventHttp(runEvent);
    }

    protected HttpProducerService getClient() {
        return httpProducerService;
    }
}
