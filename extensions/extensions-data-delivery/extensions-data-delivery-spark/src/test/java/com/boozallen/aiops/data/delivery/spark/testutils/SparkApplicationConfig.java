package com.boozallen.aiops.data.delivery.spark.testutils;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SparkApplicationConfig {
    private static String UNSAFE_LINE_DELIMITER = "# Source:";

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Spec {
        private Map<String, String> sparkConf;
        private Map<String, String> hadoopConf;

        private Spec() {}

        public Map<String, String> sparkConf() { return sparkConf; }
        public Map<String, String> hadoopConf() { return hadoopConf; }

        public void setSparkConf(Map<String, String> sparkConf) {
            this.sparkConf = sparkConf;
        }

        public void setHadoopConf(Map<String, String> hadoopConf) {
            this.hadoopConf = hadoopConf;
        }
        
        public static class Deps {
            private String[] jars;
            private String[] excludePackages;
            private String[] packages;
            private String[] repositories;

            private Deps() {}

            public String[] jars() { return jars; }
            public String[] packages() { return packages; }
            public String[] excludePackages() { return excludePackages; }
            public String[] repositories() { return repositories; }

            
            public void setJars(String[] jars) {
                this.jars = jars;
            }

            public void setPackages(String[] packages) {
                this.packages = packages;
            }

            public void setExcludePackages(String[] excludePackages) {
                this.excludePackages = excludePackages;
            }
            public void setRepositories(String[] repositories) { this.repositories = repositories; }
        }
        private Deps deps;
        public Deps deps() {
            return deps;
        }
        public void setDeps(Deps deps) {
            this.deps = deps;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        private String name;
        private String namespace;

        private Metadata() {}

        public String name() {
            return name;
        }

        public String namespace() {
            return namespace;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }

    public static SparkApplicationConfig loadApplicationSpec(File applicationFile) {
        String yamlContent = cleanTestChart(applicationFile);
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        om.findAndRegisterModules();
        SparkApplicationConfig applicationConfig;
        try {
            applicationConfig = om.readValue(yamlContent, SparkApplicationConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return applicationConfig;
    }

    private Spec spec;
    private Metadata metadata;

    public void setSpec(Spec spec) {
        this.spec = spec;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Spec spec() {
        return spec;
    }

    public Metadata metadata() {
        return metadata;
    }

    /**
     * Cleans out any warnings from the top of the generated helm chart that Helm may have written. These warnings
     * prevent the generated file from being a valid yaml file, so it needs to be cleaned up before being passed to
     * the parser.
     * @param applicationFile The File object for the test chart to parse
     * @return A string that contains the corrected yaml content
     */
    private static String cleanTestChart(File applicationFile) {
        List<String> lines = null;
        try {
            lines = Files.lines(applicationFile.toPath()).collect(Collectors.toList());
            for(Iterator<String> it = lines.iterator(); it.hasNext();) {
                String line = it.next();
                if(line.contains(UNSAFE_LINE_DELIMITER)) {
                    break;
                } else {
                    it.remove();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.join("\n", lines);
    }
}
