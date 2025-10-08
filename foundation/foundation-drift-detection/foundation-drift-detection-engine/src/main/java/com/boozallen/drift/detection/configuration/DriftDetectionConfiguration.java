package com.boozallen.drift.detection.configuration;

/*-
 * #%L
 * Drift Detection::Core
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
