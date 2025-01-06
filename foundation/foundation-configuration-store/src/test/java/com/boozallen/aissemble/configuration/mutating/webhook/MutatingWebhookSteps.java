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
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;
import io.restassured.response.ValidatableResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MutatingWebhookSteps {
    private static final String expectedInjectedValue = "env-access-key-id";
    private static final String expectedInjectedSecretValue = "env-pass";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ValidatableResponse response;
    private AdmissionReview admissionReviewRequest;
    private ConfigMap configMap;
    private Secret secret;
    private ObjectMeta objectMeta;
    private Map<String, String > configMapData;
    private Map<String, String> secretData;
    private String responsePatch;


    @Given("a ConfigMap definition that contains the substitution key exists")
    public void aConfigMapDefinitionThatContainsTheSubstituionKeyExists() {
        final String propertyKey = "groupName=aws-credentials;propertyName=AWS_ACCESS_KEY_ID";
        objectMeta = new ObjectMeta();
        HashMap<String, String> metaLabels = new HashMap();
        metaLabels.put("aissemble-configuration-store", "enabled");
        objectMeta.setLabels(metaLabels);
        configMapData = new HashMap<>();
        configMapData.put("AWS_ACCESS_KEY_ID", ConfigMutatingWebhook.CONFIG_STORE_INJECT_START + propertyKey + ConfigMutatingWebhook.CONFIG_STORE_INJECT_END);
    }

    @Given("a Secret definition that contains the encoded substitution key exists")
    public void aSecretDefinitionThatContainsTheEncodedSubstituionKeyExists() {
        final String testPasswordValue = ConfigMutatingWebhook.CONFIG_STORE_INJECT_START + "groupName=aws-credentials;propertyName=AWS_PASSWORD" + ConfigMutatingWebhook.CONFIG_STORE_INJECT_END;
        objectMeta = new ObjectMeta();
        HashMap<String, String> metaLabels = new HashMap();
        metaLabels.put("aissemble-configuration-store", "enabled");
        objectMeta.setLabels(metaLabels);

        secretData = new HashMap<>();
        secretData.put("AWS_PASSWORD", new String(org.apache.commons.codec.binary.Base64.encodeBase64(testPasswordValue.getBytes())));
    }

    @Given("a Secret definition that contains the encoded and plain text substitution key exists")
    public void aSecretDefinitionThatContainsTheEncodedAndPlainTextSubstituionKeyExists() {
        final String testPasswordValue = ConfigMutatingWebhook.CONFIG_STORE_INJECT_START + "groupName=aws-credentials;propertyName=AWS_PASSWORD" + ConfigMutatingWebhook.CONFIG_STORE_INJECT_END;
        final String propertyKey = "groupName=aws-credentials;propertyName=AWS_ACCESS_KEY_ID";

        objectMeta = new ObjectMeta();
        HashMap<String, String> metaLabels = new HashMap();
        metaLabels.put("aissemble-configuration-store", "enabled");
        metaLabels.put("TEST_META",  ConfigMutatingWebhook.CONFIG_STORE_INJECT_START + propertyKey + ConfigMutatingWebhook.CONFIG_STORE_INJECT_END);
        objectMeta.setLabels(metaLabels);

        secretData = new HashMap<>();
        secretData.put("AWS_PASSWORD", new String(org.apache.commons.codec.binary.Base64.encodeBase64(testPasswordValue.getBytes())));
    }

    @Given("the ConfigMap definition has the injection metadata label")
    public void theConfigMapDefinitionHasTheInjectionMetatdataLabel() {
        configMap = new ConfigMapBuilder()
                .withData(configMapData)
                .withMetadata(objectMeta)
                .build();
    }

    @Given("the Secret definition has the injection metadata label")
    public void theSecretDefinitionHasTheInjectionMetatdataLabel() {
        secret = new SecretBuilder()
                .withData(secretData)
                .withMetadata(objectMeta)
                .build();
    }

    @When("a kubernetes resource request is made to create a ConfigMap")
    public void aKubernetesResourceRequestIsMade() throws JsonProcessingException {
        this.admissionReviewRequest = createAdmissionReviewRequest("ConfigMap", "configmaps", configMap);

        // Convert AdmissionReview object to JSON
        String admissionReviewRequestJson = this.objectMapper.writeValueAsString(admissionReviewRequest);

        response = given()
                .contentType("application/json")
                .body(admissionReviewRequestJson)
                .when()
                .post("/webhook/process")
                .then();
    }

    @When("a kubernetes resource request is made to create a Secret")
    public void aKubernetesResourceRequestIsMadeForSecret() throws JsonProcessingException {
        this.admissionReviewRequest = createAdmissionReviewRequest("Secret", "secret", secret);

        // Convert AdmissionReview object to JSON
        String admissionReviewRequestJson = this.objectMapper.writeValueAsString(admissionReviewRequest);

        response = given()
                .contentType("application/json")
                .body(admissionReviewRequestJson)
                .when()
                .post("/webhook/process")
                .then();
    }

    @Then("the patch is returned")
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

    @Then("the Secret patch contains the encoded injected value")
    public void theSecretDefinitionContainsTheInjectedValue() throws JsonProcessingException {
        JsonNode jsonPatch = objectMapper.readTree(new String(Base64.getDecoder().decode(responsePatch)));
        String encodedInjectedSecretValue = new String(Base64.getEncoder().encode(expectedInjectedSecretValue.getBytes()));
        List<String> valuesList = jsonPatch.findValues("value").stream().map(JsonNode::asText).toList();
        assertTrue("The response patch does not contain the injected value", valuesList.contains(encodedInjectedSecretValue));
    }

    @Then("the Secret patch contains the both plain text and encoded injected value")
    public void theSecretDefinitionContainsPlainAndEncodedTheInjectedValue() throws JsonProcessingException {
        JsonNode jsonPatch = objectMapper.readTree(new String(Base64.getDecoder().decode(responsePatch)));
        String encodedInjectedSecretValue = new String(Base64.getEncoder().encode(expectedInjectedSecretValue.getBytes()));
        List<String> valuesList = jsonPatch.findValues("value").stream().map(JsonNode::asText).toList();
        assertTrue("The response patch does not contain the injected value", valuesList.contains(encodedInjectedSecretValue));
        assertTrue("The response patch does not contain the injected value", valuesList.contains(expectedInjectedValue));
    }

    /**
     * Create an {@link AdmissionReviewRequest} for testing
     */
    private AdmissionReview createAdmissionReviewRequest(String kind, String resource, KubernetesResource resourceObj) {
        AdmissionRequest request = new AdmissionRequest();
        request.setUid("example-uid");
        request.setKind(new GroupVersionKind("", kind, "v1"));
        request.setResource(new GroupVersionResource("", resource, "v1"));
        request.setName("example-pod");
        request.setNamespace("default");

        // Set the pod on the request
        request.setObject(resourceObj);

        // Create AdmissionReview with the request
        return new AdmissionReviewBuilder()
            .withApiVersion("admission.k8s.io/v1")
            .withKind("AdmissionReview")
            .withRequest(request)
            .build();
    }
}
