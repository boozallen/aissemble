package com.boozallen.aiops.data.delivery.messaging;

/*-
 * #%L
 * AIOps Foundation::AIOps Data Delivery::Spark
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.technologybrewery.krausening.Krausening;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.enterprise.inject.spi.Extension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

/**
 * A krausening-based reactive messaging config source. Helps map a pipeline-model-based configuration to the appropriate
 * reactive messaging properties. E.g., imagine a pipeline with messaging step named AddVideoData. The following
 * translations would apply.
 * |     pipeline-messaging.properties     |                 microprofile-config.properties                |
 * | AddVideoData.out.connector            | mp.messaging.outgoing.<channelName>.connector                 |
 * | AddVideoData.in.ssl.keystore.password | mp.messaging.incoming.<channelName>.ssl.keystore.password     |
 *
 * Note that the actual microprofile property that results is an internal detail of the generated pipeline model code,
 * and so is not guaranteed.  This configuration allows control over the Reactive Messaging channels, without exposing
 * those internal details.
 */
public class PipelineMessagingConfig implements ConfigSource, Extension {
    private static final Set<Step> stepList = new HashSet<>();
    private final Properties pipelineProperties;

    public PipelineMessagingConfig() {
        Properties krauseningProps = Krausening.getInstance().getProperties("pipeline-messaging.properties");
        pipelineProperties = krauseningProps != null ? krauseningProps : new Properties();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> map = new HashMap<>();
        for (String name : pipelineProperties.stringPropertyNames()) {
            String reactiveName = mapToReactiveProperty(name);
            String value = pipelineProperties.getProperty(reactiveName);
            map.put(reactiveName, value);
        }
        return map;
    }

    @Override
    public Set<String> getPropertyNames() {
        return getProperties().keySet();
    }

    @Override
    public int getOrdinal() {
        // 500 is above the microprofile-config.properties ordinal of 400, so this config takes precedence
        return 500;
    }

    @Override
    public String getValue(String property) {
        // Does it make sense to translate values as well to fully insulate from Smallrye, or is it too much maintenance
        // for not enough gain?
        return pipelineProperties.getProperty(mapToStepProperty(property));
    }

    @Override
    public String getName() {
        return "pipeline-messaging-config";
    }

    public static void registerStep(String stepName, String incomingChannel, String outgoingChannel) {
        stepList.add(new Step(stepName, incomingChannel, outgoingChannel));
    }

    private String mapToReactiveProperty(String stepProperty) {
        return stepList.stream()
                .map(step -> step.mapToReactiveProperty(stepProperty))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(stepProperty);
    }

    private String mapToStepProperty(String reactiveProperty) {
        return stepList.stream()
                .map(step -> step.mapToStepProperty(reactiveProperty))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(reactiveProperty);
    }

    public static class Step {
        private final String name;
        private final String incoming;
        private final String outgoing;

        public Step(String name, String incoming, String outgoing) {
            this.name = name;
            this.incoming = incoming;
            this.outgoing = outgoing;
        }

        public String mapToReactiveProperty(String property) {
            if (incoming != null && property.startsWith(stepInPrefix())) {
                return property.replace(stepInPrefix(), reactiveInPrefix());
            } else if (outgoing != null && property.startsWith(stepOutPrefix())) {
                return property.replace(stepOutPrefix(), reactiveOutPrefix());
            }
            return null;
        }

        public String mapToStepProperty(String property) {
            if (incoming != null && property.startsWith(reactiveInPrefix())) {
                return property.replace(reactiveInPrefix(), stepInPrefix());
            } else if (outgoing != null && property.startsWith(reactiveOutPrefix())) {
                return property.replace(reactiveOutPrefix(), stepOutPrefix());
            }
            return null;
        }

        private String stepInPrefix() {
            return stepPrefix() + ".in";
        }

        private String stepOutPrefix() {
            return stepPrefix() + ".out";
        }

        private String stepPrefix() {
            return name;
        }

        private String reactiveInPrefix() {
            return reactivePrefix() + ".incoming." + incoming;
        }

        private String reactiveOutPrefix() {
            return reactivePrefix() + ".outgoing." + outgoing;
        }

        private String reactivePrefix() {
            return "mp.messaging";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Step step = (Step) o;

            return Objects.equals(name, step.name);
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        @Override
        public String toString() {
            return name + '{' +
                    (incoming != null ? "incoming='" + incoming + "', " : "") +
                    (outgoing != null ? "outgoing='" + outgoing : "") +
                    '}';
        }
    }
}
