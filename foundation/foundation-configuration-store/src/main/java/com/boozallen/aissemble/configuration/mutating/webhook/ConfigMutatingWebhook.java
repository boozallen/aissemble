package com.boozallen.aissemble.configuration.mutating.webhook;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
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

import com.boozallen.aissemble.configuration.store.ConfigLoader;
import com.boozallen.aissemble.configuration.store.Property;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.admission.v1.AdmissionRequest;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponse;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionResponseBuilder;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReview;
import io.fabric8.kubernetes.api.model.admission.v1.AdmissionReviewBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

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

            //This scans to see if there's any config need to be injected and replace corresponding values and saves into updatedJson
            String updatedJson = replaceConfigStoreKeysWithConfigValue(json);

            JSONObject jsonObject = new JSONObject(json);
            if(request.getKind().getKind().equals("Secret") && jsonObject.has("data") ) {
                //if replaceConfigStoreKeysWithConfigValue doesn't have any injection it would return empty, so we need original json to scan.
                boolean hasPlainTextInjection = !updatedJson.isEmpty();
                String secretJson = hasPlainTextInjection ? updatedJson: json;
                updatedJson = replaceSecretConfigStoreKeyWithConfigValue(secretJson, mapper, hasPlainTextInjection);
            }

            if (!updatedJson.isEmpty()){
                JsonNode patch = JsonDiff.asJson(mapper.readTree(json), mapper.readTree(updatedJson));
                response = new AdmissionResponseBuilder()
                        .withAllowed(true)
                        .withUid(request.getUid())
                        .withPatch(Base64.getEncoder().encodeToString(patch.toString().getBytes()))
                        .withPatchType("JSONPatch")
                        .build();
                logger.info("The kubernetes resource request has been updated.");
            }else {
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


    /**
     * This method is to inject values in the data fields for any encoded Configvalues from the properties file and save it as based64 encoded.
     *
     * @param json json string to scan
     * @param mapper json object mapper
     * @param hasPlainTextInjection boolean that plain text injection is updated.
     * @return updatedJSON  If no injection, return empty.
     */
    private String replaceSecretConfigStoreKeyWithConfigValue(String json, ObjectMapper mapper, boolean hasPlainTextInjection) throws JsonProcessingException {
        String updatedJSON = json;
        JSONObject jsonObject = new JSONObject(json);
        String dataString = jsonObject.getString("data");
        Map<String, String> secretDataMap = mapper.readValue(dataString, new TypeReference<>() {});

        //key being base64 value that contains getConfigValue(), value being base64 value injected with the actual property.
        Map<String, String> injectionMap = new HashMap<>();
        for(String secretDataVal : secretDataMap.values()) {
            String decodedStr = new String(Base64.getDecoder().decode(secretDataVal));
            String updatedDecodedStr = replaceConfigStoreKeysWithConfigValue(decodedStr);
            if (!updatedDecodedStr.isEmpty()) {
                injectionMap.put(secretDataVal, new String(Base64.getEncoder().encode(updatedDecodedStr.getBytes())));
            }
        }

        for(Map.Entry<String, String> injection: injectionMap.entrySet()) {
            updatedJSON = updatedJSON.replace(injection.getKey(), injection.getValue());
        }

        if (updatedJSON.equals(json)) {
            if (hasPlainTextInjection) {
                return json; // no change in secret text, return the json with plain text injections
            } else {
                return "";  // no change in both secret and plain text, return empty
            }
        } else {
            return updatedJSON;
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
