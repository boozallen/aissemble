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

import java.util.HashMap;
import java.util.Map;

import com.boozallen.drift.detection.algorithm.DriftAlgorithm;

/**
 * {@link ShortHand} enum is used to map the {@link DriftAlgorithm} short hand
 * to the corresponding class. We can eventually use properties to do this
 * dynamically, but this sets up the basic structure of having a short hand
 * named mapped to a class name.
 * 
 * @author Booz Allen Hamilton
 *
 */
public enum ShortHand {
    
    STANDARD_DEVIATION("StandardDeviation", "com.boozallen.drift.detection.algorithm.StandardDeviationDriftAlgorithm");
    
    public final String shortHand;
    
    public final String className;
    
    private static final Map<String, String> BY_SHORT_HAND = new HashMap<>();
    
    static {
        for (ShortHand e: values()) {
            BY_SHORT_HAND.put(e.shortHand, e.className);
        }
    }
    
    private ShortHand(String shortHand, String className) {
        this.shortHand = shortHand;
        this.className = className;
    }
    
    public static boolean hasClassForShortHand(String shortHand) {
        boolean hasMatchingClass = false;
        if (BY_SHORT_HAND.containsKey(shortHand)) {
            hasMatchingClass = true;
        }
        return hasMatchingClass;
    }
    
    public static String getClassNameForShortHand(String shortHand){
        String clazz = null;
        if (BY_SHORT_HAND.containsKey(shortHand)) {
            clazz = BY_SHORT_HAND.get(shortHand);
        }
        return clazz;
    }

}
