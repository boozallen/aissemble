package com.boozallen.aissemble.datalineage.consumer;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Lineage::Http Consumer Service
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

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
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
