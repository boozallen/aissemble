package com.boozallen.drift.detection.util;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.data.DriftVariables;

public class TestDataUtil {

    public static double createRandomDouble() {
        return createRandomDouble(1, 100);
    }

    public static double createRandomDouble(int low, int high) {
        return Double.parseDouble(RandomUtils.nextInt(low, high) + "." + RandomStringUtils.randomNumeric(2, 9));
    }

    public static DriftVariables<Double> createRandomDriftVariables() {
        return createRandomDriftVariables(1, 100);
    }

    public static DriftVariable<Double> createRandomDriftVariable() {
        return createRandomDriftVariable(1, 100);
    }

    public static DriftDetectionResult createRandomResult() {
        DriftDetectionResult result = new DriftDetectionResult();
        result.setDriftDiscovered(RandomUtils.nextBoolean());
        result.setTimestamp(Instant.now());
        result.addPolicyIdentifier(RandomStringUtils.randomAlphabetic(10));
        result.addDataName(RandomStringUtils.randomAlphabetic(5));
        return result;
    }

    public static DriftVariables<Double> createRandomDriftVariables(int low, int high) {

        // Create a random number of test data
        int random = RandomUtils.nextInt(3, 8);
        List<DriftVariable<Double>> values = new ArrayList<DriftVariable<Double>>();
        for (int i = 1; i <= random; i++) {
            DriftVariable<Double> value = createRandomDriftVariable(low, high);
            values.add(value);
        }
        DriftVariables<Double> variables = new DriftVariables<Double>(values);
        variables.setName(RandomStringUtils.randomAlphabetic(8));

        return variables;
    }

    public static DriftVariable<Double> createRandomDriftVariable(int low, int high) {
        DriftVariable<Double> variable = new DriftVariable<Double>(createRandomDouble(low, high));
        variable.setName(RandomStringUtils.randomAlphabetic(5));
        return variable;
    }

}
