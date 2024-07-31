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

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.fabric8.kubernetes.api.model.GroupVersionKind;
import io.fabric8.kubernetes.api.model.GroupVersionResource;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;
import io.restassured.response.ValidatableResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MutatingWebhookSteps {
    private ValidatableResponse response;
    private AdmissionReview admissionReviewRequest;
    private ObjectMapper objectMapper = new ObjectMapper();

    @When("a kubernetes resource request is made")
    public void aKubernetesResourceRequestIsMade() throws JsonProcessingException {
        this.admissionReviewRequest = createAdmissionReviewRequest();

        // Convert AdmissionReview object to JSON
        String admissionReviewRequestJson = this.objectMapper.writeValueAsString(admissionReviewRequest);

        response = given()
                .contentType("application/json")
                .body(admissionReviewRequestJson)
                .when()
                .post("/webhook/process")
                .then();
    }

    @Then("the processed kubernetes resource is returned")
    public void theProcessedKubernetesResourceIsReturned() throws JsonMappingException, JsonProcessingException {
        response.statusCode(200);

        // Read in the response and verify it has the correct attributes
        AdmissionReview admissionReviewResponse = this.objectMapper.readValue(response.extract().response().asString(), AdmissionReview.class);

        assertTrue("The response should have allowed set to true", admissionReviewResponse.getResponse().getAllowed());
        assertEquals("The response uid did not match the request uid", this.admissionReviewRequest.getRequest().getUid(), admissionReviewResponse.getResponse().getUid());
        assertEquals("The response api version did not match the request api version", this.admissionReviewRequest.getApiVersion(), admissionReviewResponse.getApiVersion());
    }
 
    /**
     * Create an {@link AdmissionReviewRequest} for testing
     */
    private AdmissionReview createAdmissionReviewRequest() {
        AdmissionRequest request = new AdmissionRequest();
        request.setUid("example-uid");
        request.setKind(new GroupVersionKind("", "Pod", "v1"));
        request.setResource(new GroupVersionResource("", "pods", "v1"));
        request.setName("example-pod");
        request.setNamespace("default");

        // Create a Pod object
        Pod pod = new PodBuilder()
            .withApiVersion("v1")
            .withKind("Pod")
            .withMetadata(new ObjectMetaBuilder().withName("example-pod").build())
            .build();

        // Set the pod on the request
        request.setObject(pod);

        // Create AdmissionReview with the request
        AdmissionReview admissionReviewRequest = new AdmissionReviewBuilder()
            .withApiVersion("admission.k8s.io/v1")
            .withKind("AdmissionReview")
            .withRequest(request)
            .build();

        return admissionReviewRequest;
    }
}
