package com.boozallen.aissemble.util;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.configuration.ConfigStoreInit;
import com.boozallen.aissemble.configuration.store.Property;
import io.quarkus.runtime.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ConfigStoreInit loads configuration properties as application starts up
 */
@Startup
@ApplicationScoped
public class ConfigStoreInitTestHelper extends ConfigStoreInit {
    private static final Logger logger = LoggerFactory.getLogger(ConfigStoreInitTestHelper.class);

    @PostConstruct
    protected void init() {
        prepareTestEnv();
        super.init();
    }

    private void prepareTestEnv() {
        Map<String, List<Property>> expectedProperties = createExpectedProperties();
        Set<String> files = expectedProperties.keySet();
        for (String file : files) {
            createAndSaveYaml(file, expectedProperties.get(file));
        }
    }

    public Map<String, List<Property>> createExpectedProperties() {
        Map<String, List<Property>> configurations = new HashMap<>();
        ArrayList<Property> properties = new ArrayList<>();
        properties.add(new Property("model-training-api", "AWS_SECRET_ACCESS_KEY", "base-secret-access-key"));
        properties.add(new Property("model-training-api", "AWS_ACCESS_KEY_ID", "base-access-key-id"));
        configurations.put("base/file1.yaml", properties);

        properties = new ArrayList<>();
        properties.add(new Property("data-lineage", "serializer", "apache.StringSerializer"));
        properties.add(new Property("data-lineage", "topic", "lineage-topic"));
        properties.add(new Property("data-lineage", "connector", "smallrye-kafka"));
        properties.add(new Property("data-lineage", "cloud-events", "false"));
        configurations.put("base/file2.yaml", properties);

        properties = new ArrayList<>();
        properties.add(new Property("messaging", "connector", "smallrye-kafka"));
        properties.add(new Property("messaging", "serializer", "apache.StringSerializer"));
        properties.add(new Property("messaging", "topic", "messaging-topic"));
        configurations.put("base/file3.yaml", properties);

        properties = new ArrayList<>();
        properties.add(new Property("model-training-api", "AWS_SECRET_ACCESS_KEY", "env-secret-access-key"));
        properties.add(new Property("model-training-api", "AWS_ACCESS_KEY_ID", "env-access-key-id"));
        configurations.put("example-env/file4.yaml", properties);

        properties = new ArrayList<>();
        properties.add(new Property("data-lineage", "added-property", "example-value"));
        properties.add(new Property("data-lineage", "cloud-events", "true"));
        configurations.put("example-env/file5.yaml", properties);

        return configurations;
    }

    private void createAndSaveYaml(String file, List<Property> properties) {
        String groupName = properties.get(0).getGroupName();
        // Create new YAML file
        Map<String, Object> root = new LinkedHashMap<>();
        ArrayList<Map<String, Object>> propertyList = new ArrayList<>();
        for (Property property: properties) {
            propertyList.add(Map.of("name", property.getName(), "value", property.getValue()));
        }
        root.put("groupName", groupName);
        root.put("properties", propertyList);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        FileWriter writer;
        try {
            String TMP_DIR = "/tmp/configurations/";
            File theDir = new File(TMP_DIR + file.split("/")[0]);
            if (!theDir.exists()){
                theDir.mkdirs();
            }
            writer = new FileWriter(TMP_DIR + file);
            yaml.dump(root, writer);
            writer.close();
        } catch (IOException e) {
            logger.error("Error creating test yamls: ", e);
        }
    }
}
