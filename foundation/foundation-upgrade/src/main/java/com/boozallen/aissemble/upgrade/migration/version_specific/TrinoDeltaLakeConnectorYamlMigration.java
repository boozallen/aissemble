package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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

import com.boozallen.aissemble.upgrade.migration.AbstractAissembleMigration;
import com.boozallen.aissemble.upgrade.util.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.baton.BatonException;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.boozallen.aissemble.upgrade.util.YamlUtils.indent;
import static org.technologybrewery.baton.util.FileUtils.readAllFileLines;
import static org.technologybrewery.baton.util.FileUtils.writeFile;

/**
 * To enable delta lake table data access by default, this migration will add the delta lake connector in the trino chart
 * values.yaml file.
 */
public class TrinoDeltaLakeConnectorYamlMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(TrinoDeltaLakeConnectorYamlMigration.class);
    private static final String CATALOGS = "catalogs:";

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            YamlUtils.YamlObject catalogs = getTrinoCatalogs(file);
            return catalogs!= null && !hasDeltaLakeConnector(catalogs);
        } catch (YAMLException e) {
            logger.warn("Failed to parse YAML file, likely because it is a Helm template: {}", file.getName(), e);
        } catch (IOException e) {
            throw new BatonException("Failed to read YAML file.", e);
        }
        return false;
    }

    @Override
    protected boolean performMigration(File file) {
        try {
            List<String> updatedContent = new ArrayList<>();
            List<String> lines = readAllFileLines(file);
            int indentSpaces = YamlUtils.getIndentSpaces(lines, 0);
            boolean inCatalogs = false;
            for (int i = 0; i < lines.size(); i++) {
                String trimmedLine = lines.get(i).trim();
                if (inCatalogs) {
                    updatedContent.add(getDeltaLakeConnectorConfig(indentSpaces));
                    inCatalogs = false;
                }
                if(trimmedLine.equals(CATALOGS)) {
                    inCatalogs = true;
                }
                updatedContent.add(lines.get(i));
            }

            writeFile(file, updatedContent);
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to migration YAML file.", e);
        }
    }

    private boolean hasDeltaLakeConnector(YamlUtils.YamlObject catalogs) {
        try {
            for (String key: catalogs.keySet()) {
                String deltaConfig = catalogs.getString(key);
                boolean hasDeltaLake =  deltaConfig.contains("connector.name=delta_lake");
                if (hasDeltaLake) {
                    return true;
                }
            }

        } catch (Exception e) {
            logger.info("Delta Lake connector not found in trino.catalogs configuration");
        }
        return false;
    }

    private String getDeltaLakeConnectorConfig(int indentSpaces) {
        return indent(3, indentSpaces) + "delta: |\n" +
                indent(4, indentSpaces) + "connector.name=delta_lake\n" +
                indent(4, indentSpaces) + "hive.metastore.uri=thrift://hive-metastore-service:9083\n" +
                indent(4, indentSpaces) + "fs.native-s3.enabled=true\n" +
                indent(4, indentSpaces) + "s3.aws-access-key=${ENV:AWS_ACCESS_KEY_ID}\n" +
                indent(4, indentSpaces) + "s3.aws-secret-key=${ENV:AWS_SECRET_ACCESS_KEY}\n" +
                indent(4, indentSpaces) + "s3.endpoint=http://s3-local:4566\n" +
                indent(4, indentSpaces) + "s3.region=us-east-1\n" +
                indent(4, indentSpaces) + "s3.path-style-access=true";
    }

    private YamlUtils.YamlObject getTrinoCatalogs(File file) throws IOException {
        YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
        if (yaml.hasObject("aissemble-trino-chart")) {
            yaml = yaml.getObject("aissemble-trino-chart");
        }
        if (yaml.hasObject("trino", "catalogs")) {
            return yaml.getObject("trino", "catalogs");
        }
        return null;
    }
}
