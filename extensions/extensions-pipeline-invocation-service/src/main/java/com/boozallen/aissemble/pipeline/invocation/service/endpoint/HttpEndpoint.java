package com.boozallen.aissemble.pipeline.invocation.service.endpoint;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Pipeline Invocation Service
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

import com.boozallen.aissemble.pipeline.invocation.service.PipelineInvocationAgent;
import com.boozallen.aissemble.pipeline.invocation.service.serialization.PipelineInvocationRequest;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;

/**
 * Entrypoint for HTTP-based requests for SparkApplication submission.
 */
@Path("/invoke-pipeline")
@ApplicationScoped
public class HttpEndpoint {
    private static final Logger logger = Logger.getLogger(HttpEndpoint.class);

    @Inject
    private PipelineInvocationAgent pipelineInvocationAgent;

    @GET
    @Path("/healthcheck")
    public Response healthCheck() throws IOException {
        return Response.ok().entity("Service is available").build();
    }

    /**
     * Receives a message requesting the submission of a SparkApplication to the cluster for processing
     * @param pipelineInvocationRequest object representing the required parameters for submitting the desired SparkApplication.
     */
    @POST
    @Path("/start-spark-operator-job")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response startSparkOperatorJob(PipelineInvocationRequest pipelineInvocationRequest) {
        try {
            logger.info("Received HTTP request to submit " + pipelineInvocationRequest.getApplicationName() + ".");
            pipelineInvocationAgent.submitSparkApplication(pipelineInvocationRequest);
            logger.info("Submitted " + pipelineInvocationRequest.getApplicationName() + " for processing.");
        } catch (Exception ex) {
            return Response.serverError().entity("Failed to submit pipeline, see service pod logs for additional details.").build();
        }
        return Response.ok().entity("Submitted " + pipelineInvocationRequest.getApplicationName()).build();
    }

}

