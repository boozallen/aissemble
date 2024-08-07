package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;

/**
 * This generator is used to generate the port map for the kubernetes deployment.
 * For now, it will generate two random ports for the http and grpc ports, intended for the inference service.
 */

public class KubernetesPortMapGenerator extends AbstractKubernetesGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                    | Template                                              | Generated File                   |
     * |---------------------------|-------------------------------------------------------|----------------------------------|
     * | inferenceValuesFile       | deployment/inference/inference.values.yaml.vm         | apps/${appName}/values.yaml      |
     * | inferenceValuesDevFileV2  | deployment/inference/v2/inference.values-dev.yaml.vm  | apps/${appName}/values-dev.yaml  |
     * | inferenceValuesFileV2     | deployment/inference/v2/inference.values.yaml.vm      | apps/${appName}/values.yaml      |
     */

    private static final int PORT_RANGE_MIN = 5000;
    private static final int PORT_NUMBER = 7080; 
    private static final int PORT_NUMBER2 = 7081;
    private static final int PORT_RANGE_MAX = 10000;

    // track the ports used during this execution
    private static HashSet<Integer> usedPorts = new HashSet<>();

    /**
     * Constructor for the KubernetesPortMapGenerator.
     * @param velocityProperty the velocity property
     * 
     * 
     */
    @Override
    protected VelocityContext configureWithoutGeneration(GenerationContext generationContext) {
        final VelocityContext vc = super.configureWithoutGeneration(generationContext);

        // You can get the appName here and add it to the velocity context
        String appName = (String) vc.get(APP_NAME);
        vc.put(APP_NAME, appName);

        // Add the logic for generating and adding random ports to the velocity context
        vc.put("port1", generateRandomPort(appName, "port1"));
        vc.put("port2", generateRandomPort(appName, "port2"));

        return vc;
    }

    /**
     * Find an available port number.
     * @param portKey the name of the port
     * @param usedPorts a list of ports that are already in use
     * @return a random port number
     * 
     */
    private int findAvailablePort(String portKey, Set<Integer> usedPorts) {
        final int MAX_ATTEMPTS = 100; // max number of attempts to check for port availability
        int attempts = 0;
        int port;

        do {
            port = generateHashedPort(portKey);
            attempts++;
        } while (usedPorts.contains(port) && attempts <= MAX_ATTEMPTS);

        // If an available port wasn't found, provide a fallback port
        if (usedPorts.contains(port)) {
            port = portKey.equals("port1") ? PORT_NUMBER : PORT_NUMBER2;
        }

        return port;
    }

    /**
     * Generate a random port number.
     * @param appName the name of the application
     * @param portKey the name of the port
     * @return a random port number.
     */
    private int generateRandomPort(String appName, String portKey) {    
        int port = findAvailablePort(appName + portKey, usedPorts);
        // Since findAvailablePort() now always returns a valid port number, we don't need to check for -1 anymore
        usedPorts.add(port);
        return port;
    }

    /**
     * Generate a random port number based on the hash of the port name.
     * @param portKey the name of the port
     * @return a random port number
     * 
     */
    private int generateHashedPort(String portKey) {
        int hash = Math.abs(portKey.hashCode());
        return PORT_RANGE_MIN + hash % (PORT_RANGE_MAX - PORT_RANGE_MIN);
    }
}
