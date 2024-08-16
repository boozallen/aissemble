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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.GroupVersionKind;
import io.fabric8.kubernetes.api.model.GroupVersionResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;
import io.restassured.response.ValidatableResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class MutatingWebhookSteps {
    private static final String expectedInjectedValue = "env-access-key-id";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ValidatableResponse response;
    private AdmissionReview admissionReviewRequest;
    private ConfigMap configMap;
    private Map<String, String > configMapData;
    private String responsePatch;

    @Given("a ConfigMap definition that contains the substitution key exists")
    public void aConfigMapDefinitionThatContainsTheSubstituionKeyExists() {
        final String propertyKey = "groupName=aws-credentials;propertyName=AWS_ACCESS_KEY_ID";
        configMapData = new HashMap<>();
        configMapData.put("AWS_ACCESS_KEY_ID", ConfigMutatingWebhook.CONFIG_STORE_INJECT_START + propertyKey + ConfigMutatingWebhook.CONFIG_STORE_INJECT_END);
    }

    @Given("the ConfigMap definition has the injection metadata label")
    public void theConfigMapDefinitionHasTheInjectionMetatdataLabel() {
        HashMap<String, String> metaLabels = new HashMap();
        metaLabels.put("aissemble-configuration-store", "enabled");
        ObjectMeta objectMeta = new ObjectMeta();
        objectMeta.setLabels(metaLabels);
        configMap = new ConfigMapBuilder()
                .withData(configMapData)
                .withMetadata(objectMeta)
                .build();
    }

    @When("a kubernetes resource request is made to create a ConfigMap")
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

    @Then("the ConfigMap patch is returned")
    public void theProcessedKubernetesResourceIsReturned() throws JsonProcessingException {
        response.statusCode(200);

        // Read in the response and verify it has the correct attributes
        AdmissionReview admissionReviewResponse = this.objectMapper.readValue(response.extract().response().asString(), AdmissionReview.class);
        assertTrue("The response should have allowed set to true", admissionReviewResponse.getResponse().getAllowed());
        assertEquals("The response uid did not match the request uid", this.admissionReviewRequest.getRequest().getUid(), admissionReviewResponse.getResponse().getUid());
        assertEquals("The response api version did not match the request api version", this.admissionReviewRequest.getApiVersion(), admissionReviewResponse.getApiVersion());

        responsePatch = admissionReviewResponse.getResponse().getPatch();
        assertNotNull(responsePatch);
    }

    @Then("the ConfigMap patch contains the injected value")
    public void theConfigMapDefinitionContainsTheInjectedValue() throws JsonProcessingException {
        JsonNode jsonPatch = objectMapper.readTree(new String(Base64.getDecoder().decode(responsePatch)));
        assertEquals("The response patch does not contain the injected value", expectedInjectedValue, jsonPatch.findValue("value").asText());
    }
    /**
     * Create an {@link AdmissionReviewRequest} for testing
     */
    private AdmissionReview createAdmissionReviewRequest() {
        AdmissionRequest request = new AdmissionRequest();
        request.setUid("example-uid");
        request.setKind(new GroupVersionKind("", "ConfigMap", "v1"));
        request.setResource(new GroupVersionResource("", "configmaps", "v1"));
        request.setName("example-pod");
        request.setNamespace("default");

        // Set the pod on the request
        request.setObject(configMap);

        // Create AdmissionReview with the request
        return new AdmissionReviewBuilder()
            .withApiVersion("admission.k8s.io/v1")
            .withKind("AdmissionReview")
            .withRequest(request)
            .build();
    }
}
