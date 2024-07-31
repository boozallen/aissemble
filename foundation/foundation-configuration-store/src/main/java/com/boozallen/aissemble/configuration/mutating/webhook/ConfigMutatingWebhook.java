package com.boozallen.aissemble.configuration.mutating.webhook;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponse;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponseBuilder;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Provides a quarkus endpoint for our MutatatingWebhook that is created as part of the universal configuration
 * helm chart. This endpoint is used to modify the incoming kubernetes resource with values from the config store.
 */
@Path("/webhook")
@ApplicationScoped
public class ConfigMutatingWebhook {
    private static final Logger logger = LoggerFactory.getLogger(ConfigMutatingWebhook.class);
    
    @POST
    @Path("/process")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AdmissionReview validate(AdmissionReview admissionReviewRequest) {
        logger.info("Webhook was run!");
        logger.info("Request: {}", admissionReviewRequest.getRequest().getKind().toString());
        
        // Currently the response does contain any patches, resulting in the requested resource remaining the same
        // TODO: create a patch for injecting values into the yaml
        AdmissionRequest request = admissionReviewRequest.getRequest();

        AdmissionResponse response = new AdmissionResponseBuilder()
                                                    .withAllowed(true)
                                                    .withUid(request.getUid())
                                                    .build();

        AdmissionReview admissionReviewResponse = new AdmissionReviewBuilder()
                                                .withResponse(response)
                                                .withApiVersion(admissionReviewRequest.getApiVersion())
                                                .build();

        return admissionReviewResponse;
    }
 }
 