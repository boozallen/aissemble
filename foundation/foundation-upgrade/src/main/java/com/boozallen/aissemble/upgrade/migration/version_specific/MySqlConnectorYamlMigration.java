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
import java.nio.file.Files;
import java.util.List;

/**
 * To resolve the CVE-2023-22102 issue, this migration will replace the mysql-connector-java jar to be the mysql-connector-j jar in
 * the values.yaml files.
 */
public class MySqlConnectorYamlMigration extends AbstractAissembleMigration {
    private static final Logger logger = LoggerFactory.getLogger(MySqlConnectorYamlMigration.class);

    @Override
    protected boolean shouldExecuteOnFile(File file) {
        try {
            YamlUtils.YamlObject yaml = YamlUtils.loadYaml(file);
            if (yaml.hasObject("sparkApp")) {
               yaml = yaml.getObject("sparkApp");
            }
            if (yaml.hasList("spec", "deps", "packages")) {
                List<String> pkgs = yaml.getListOfStrings("spec", "deps", "packages");
                return pkgs.stream().anyMatch(pkg -> pkg.equals("mysql:mysql-connector-java:8.0.30"));
            }
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
            Files.writeString(file.toPath(), Files.readString(file.toPath())
                    .replace("mysql:mysql-connector-java:8.0.30", "com.mysql:mysql-connector-j:9.2.0"));
            return true;
        } catch (IOException e) {
            throw new BatonException("Failed to update mysql-connector-java jars in: " + file.getPath(), e);
        }
    }
}
