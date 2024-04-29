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

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import java.util.concurrent.CompletionStage;

/**
 * Rest Client Bean for posting to the requested HTTP endpoint
 */

@RegisterRestClient
public interface HttpProducerService {
    @POST
    @Produces("application/json")
    CompletionStage<String> postEventHttp(String runEvent);
}
