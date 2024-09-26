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
 
import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponse;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponseBuilder;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Provides a quarkus endpoint for our MutatatingWebhook that is created as part of the universal configuration
 * helm chart. This endpoint is used to modify the incoming kubernetes resource with values from the config store.
 */
@Path("/webhook")
@ApplicationScoped
public class ConfigMutatingWebhook {
    public static final String KRAUSENING_PASSWORD = "KRAUSENING_PASSWORD";
    public static final String CONFIG_STORE_INJECT_START = "$getConfigValue(";
    public static final String CONFIG_STORE_INJECT_END = ")";
    public static final String PROPERTY_KEY_DELIMITER = ";";
    public static final String GROUP_NAME = "groupName";
    public static final String PROPERTY_NAME = "propertyName";
    private static final Logger logger = LoggerFactory.getLogger(ConfigMutatingWebhook.class);

    @Inject
    public ConfigLoader configLoader;
    
    @POST
    @Path("/process")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AdmissionReview validate(AdmissionReview admissionReviewRequest) {
        String masterPassword = System.getProperty(KRAUSENING_PASSWORD);
        if (StringUtils.isBlank(masterPassword) && StringUtils.isBlank(System.getenv(KRAUSENING_PASSWORD))) {
            logger.warn("No {} set, the encryped property value will remain encrypted!", KRAUSENING_PASSWORD);
        }
        logger.info("Webhook intercepts kubernetes resource request: [name: {}, kind: {}]", admissionReviewRequest.getRequest().getName(), admissionReviewRequest.getRequest().getKind().getKind());

        AdmissionRequest request = admissionReviewRequest.getRequest();

        JsonFactory factory = JsonFactory.builder()
                .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
                .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
                .enable(JsonReadFeature.ALLOW_YAML_COMMENTS)
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .build();

        ObjectMapper mapper = new ObjectMapper(factory);
        AdmissionResponse response;
        try {
            String json = mapper.writeValueAsString(request.getObject());
            String updatedJson = replaceConfigStoreKeysWithConfigValue(json);

            if (!updatedJson.isEmpty()) {
                JsonNode patch = JsonDiff.asJson(mapper.readTree(json), mapper.readTree(updatedJson));
                response = new AdmissionResponseBuilder()
                        .withAllowed(true)
                        .withUid(request.getUid())
                        .withPatch(Base64.getEncoder().encodeToString(patch.toString().getBytes()))
                        .withPatchType("JSONPatch")
                        .build();
                logger.info("The kubernetes resource request has been updated.");
            } else {
                response = new AdmissionResponseBuilder()
                        .withAllowed(true)
                        .withUid(request.getUid())
                        .build();
                logger.info("No updates have made to the kubernetes resource request.");
            }

        } catch (Exception e) {
            logger.error("Error injecting the config store value", e);
            // create failed response
            response = new AdmissionResponseBuilder()
                    .withAllowed(false)
                    .withUid(request.getUid())
                    .withStatus(new StatusBuilder().withStatus("Failed").withCode(500).build())
                    .build();
        }

        return new AdmissionReviewBuilder()
                .withResponse(response)
                .withApiVersion(admissionReviewRequest.getApiVersion())
                .build();
    }

    private String getProperty(String groupName,String propertyName) {
        PropertyKey propertyKey = new PropertyKey(groupName, propertyName);
        Property property;
        try {
            property = this.configLoader.read(propertyKey);
            return property.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String replaceConfigStoreKeysWithConfigValue(String json) {
        StringBuilder updatedJson = new StringBuilder();
        String[] data = json.split(Pattern.quote(CONFIG_STORE_INJECT_START));
        // if the config store key is found
        if (data.length > 1) {
            updatedJson.append(data[0]);
            logger.info("Config store properties injection requested: ");
            for (var i=1; i<data.length; i++) {
                int contentIndex = data[i].indexOf(CONFIG_STORE_INJECT_END);
                String[] key = data[i].substring(0, contentIndex).split(PROPERTY_KEY_DELIMITER);
                Map<String, String> keyMap = convertToKeyMap(key);
                String value = getProperty(keyMap.get(GROUP_NAME), keyMap.get(PROPERTY_NAME));
                updatedJson.append(value);
                if (data[i].length() > contentIndex + 1) {
                    updatedJson.append(data[i].substring(contentIndex+1));
                }
            }
        }

        return updatedJson.toString();
    }

    private Map<String, String> convertToKeyMap(String[] key) {
        Map<String, String> map = new HashMap<>();

        for (String item : key) {
            String[] parts = item.split("=");
            map.put(parts[0], parts[1]);
        }

        return map;
    }
 }
 