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

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
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
