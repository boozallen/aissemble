package com.boozallen.aissemble.upgrade.migration.v1_11_0;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * This migration removes hive username in the hive-metastore-service values.yaml to use values from configuration store service only if values.yaml has default value.
 */
public class SparkInfrastructureUniversalConfigYAMLMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(SparkInfrastructureUniversalConfigYAMLMigration.class);
    private static final List<String> propertiesForConfigStore = Arrays.asList("javax.jdo.option.ConnectionUserName");
    private static final List<String> mysqlAuthForConfigStore = Arrays.asList("username");
    private static final HashSet<String> linesToRemove = new HashSet<>(Arrays.asList("username: hive", "- name: javax.jdo.option.ConnectionUserName" ,"value: hive", "description: Username to use against metastore database", "# NB: UCS"));
    private static final String DEFAULT_METASTORE_DB_USERNAME = "hive";
    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
            if(yaml.hasObject("aissemble-hive-metastore-service-chart"))
            {
                yaml = yaml.getObject("aissemble-hive-metastore-service-chart");
            }else{
                return false;
            }
            boolean hasMySqlAuthDefaultUsername = false;
            if (yaml.hasObject("mysql", "auth")) {
                YamlUtils.YamlObject mysqlAuth = yaml.getObject("mysql", "auth");
                for(String key : mysqlAuth.keySet())
                {
                    if(mysqlAuthForConfigStore.contains(key) && mysqlAuth.get(key).equals(DEFAULT_METASTORE_DB_USERNAME))
                    {
                        hasMySqlAuthDefaultUsername = true;
                        break;
                    }
                }

            }
            boolean hasMetastoreServiceConfigDefaultUsername = false;
            if (yaml.hasList("configMap", "metastoreServiceConfig", "properties")) {
                List<YamlUtils.YamlObject> properties = yaml.getListOfObjects("configMap", "metastoreServiceConfig", "properties");
                for(YamlUtils.YamlObject property : properties)
                {
                    if(propertiesForConfigStore.contains(property.get("name")) && property.get("value").equals(DEFAULT_METASTORE_DB_USERNAME))
                    {
                        hasMetastoreServiceConfigDefaultUsername = true;
                        break;
                    }
                   }
            }
            return hasMySqlAuthDefaultUsername && hasMetastoreServiceConfigDefaultUsername;

        } catch (YAMLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse values yaml file: {}", file.getPath(), e);
            } else {
                logger.info("Failed to parse values yaml file: {}", file.getName());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            File tempFile = new File("tempValuesFile.yaml");
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                boolean skipConnectionPassword = false;
                if(i >= 1 && lines.get(i-1).trim().equals("- name: javax.jdo.option.ConnectionPassword"))
                {
                    skipConnectionPassword = true;
                }
                if(linesToRemove.contains(line) && !skipConnectionPassword)
                {
                    continue;
                }
                writer.write(lines.get(i) + System.getProperty("line.separator"));
            }
            writer.close();

            return tempFile.renameTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
