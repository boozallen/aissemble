package com.boozallen.drift.detection.configuration;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import org.aeonbits.owner.KrauseningConfig;
import org.aeonbits.owner.KrauseningConfig.KrauseningSources;

/**
 * {@link DriftDetectionConfiguration} is used to configure the drift detection
 * service and defaults.
 * 
 * @author Booz Allen Hamilton
 *
 */
@KrauseningSources(value = "drift-detection.properties")
public interface DriftDetectionConfiguration extends KrauseningConfig {

    /**
     * Configures the location and file name of the file that contains the drift
     * policies.
     * 
     * @return the location of the file that contains the drift policies
     */
    @Key("drift-policies-location")
    String getDriftPoliciesLocation();

    /**
     * Configures the default package for the algorithm implementations. This
     * allows commonly used algorithms to be referenced by short-hand (just the
     * classname), instead of having to type out the fully qualified name every
     * time.
     * 
     * @return the default algorithm package that is checked if an algorithm is
     *         using a class name instead of fully qualified name.
     */
    @Key("default-algorithm-package")
    @DefaultValue("com.boozallen.drift.detection.algorithm")
    String getDefaultAlgorithmPackage();

}
