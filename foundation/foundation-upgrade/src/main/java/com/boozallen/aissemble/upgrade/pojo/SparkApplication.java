package com.boozallen.aissemble.upgrade.pojo;

import java.util.List;
import java.util.Map;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import lombok.Data;

/**
 * Java object to represent a SparkApplication yaml file.
 */
@Data
public class SparkApplication implements AbstractYamlObject {
    private SparkApp sparkApp;

    /**
     * Function to check whether a {@link SparkApplication} instance contains any driver environment variables.
     * @return {@link List}<{@link Env}> for the driver
     */
    public List<Env> getDriverEnvs() {
        return this.sparkApp != null ? this.sparkApp.getDriverEnvs(): null;
    }

    /**
     * Function to check whether a {@link SparkApplication} instance contains any executor environment variables.
     * @return {@link List}<{@link Env}> for the executor
     */
    public List<Env> getExecutorEnvs() {
        return this.sparkApp != null ? this.sparkApp.getExecutorEnvs(): null;
    }

    /**
     * Function to check whether a {@link SparkApplication} instance contains any driver envFrom variables.
     * @return {@link Boolean} result
     */
    public Boolean hasDriverEnvFrom() {
        return this.sparkApp != null ? this.sparkApp.hasDriverEnvFrom(): false;
    }

    /**
     * Function to check whether a {@link SparkApplication} instance contains any executor envFrom variables.
     * @return {@link Boolean} result
     */
    public Boolean hasExecutorEnvFrom() {
        return this.sparkApp != null ? this.sparkApp.hasExecutorEnvFrom(): false;
    }

    @Data
    public static class SparkApp {
        private Spec spec;

        public List<Env> getDriverEnvs() {
            return this.spec != null ? this.spec.getDriverEnvs(): null;
        }

        public List<Env> getExecutorEnvs() {
            return this.spec != null ? this.spec.getExecutorEnvs(): null;
        }

        public Boolean hasDriverEnvFrom() {
            return this.spec != null ? this.spec.hasDriverEnvFrom(): false;
        }

        public Boolean hasExecutorEnvFrom() {
            return this.spec != null ? this.spec.hasExecutorEnvFrom(): false;
        }
        
    }

    @Data
    public static class Spec {
        private Driver driver;
        private Executor executor;

        public List<Env> getDriverEnvs() {
            return this.driver != null ? this.driver.getEnv(): null;
        }

        public List<Env> getExecutorEnvs() {
            return this.executor != null ? this.executor.getEnv(): null;
        }

        public Boolean hasDriverEnvFrom() {
            return this.driver != null ? this.driver.hasDriverEnvFrom(): false;
        }

        public Boolean hasExecutorEnvFrom() {
            return this.executor != null ? this.executor.hasExecutorEnvFrom(): false;
        }
    }

    @Data
    public static class Driver {
        private List<Env> env;
        private List<Object> envFrom;

        public Boolean hasDriverEnvFrom() {
            return this.envFrom != null ? true : false;
        }
    }

    @Data
    public static class Executor {
        private List<Env> env;
        private List<Object> envFrom;

        public Boolean hasExecutorEnvFrom() {
            return this.envFrom != null ? true : false;
        }
    }

    @Data
    public static class Env {
        private String name;
        private String value;
    }
}