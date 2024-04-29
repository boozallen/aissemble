package com.boozallen.drift.detection.algorithm;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.DriftDetectionException;
import com.boozallen.drift.detection.DriftDetectionResult;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.data.DriftVariable;
import com.boozallen.drift.detection.data.DriftVariables;

public class StandardDeviationDriftAlgorithm extends AbstractDriftAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(StandardDeviationDriftAlgorithm.class);

    private static final String DEFAULT_ZSCORE = "1";

    private static final int NUMBER_DECIMAL_PLACES = 6;

    public static final String MEAN_CONFIGURATION = "mean";

    public static final String STANDARD_DEVIATION_CONFIGURATION = "standardDeviation";

    public static final String ZSCORE_CONFIGURATION = "zScore";

    public static final String UPPER_BOUND_CONFIGURATION = "upperBound";

    public static final String LOWER_BOUND_CONFIGURATION = "lowerBound";

    private Double mean = null;

    private Double standardDeviation = null;

    private Double zScore = new Double(DEFAULT_ZSCORE);

    private Double upperBound = null;

    private Double lowerBound = null;

    @Override
    public DriftDetectionResult calculateDrift() {

        // Currently not applicable to this algorithm, so ignore or decide a
        // pattern to handle.
        throw new DriftDetectionException("Method currently not implemented");
    }

    @Override
    public DriftDetectionResult calculateDrift(DriftData input) {

        // Go run it against the input data but first make sure we have enough
        // data
        if (upperBound == null || lowerBound == null) {
            throw new DriftDetectionException(
                    "Not enough information to detect drift. Either add configurations to the rule or pass in control data");
        }

        return detectDrift(mean, standardDeviation, upperBound, lowerBound, input);
    }

    // Calculate drift based on a set of input data and a set of control
    // data
    @Override
    public DriftDetectionResult calculateDrift(DriftData input, DriftData control) {

        // validate and convert the data
        double[] controlValues = validateAndConvertControl(control);

        // Calculate control data
        Double calculatedMean = calculateMean(controlValues);
        logger.info("Mean calculated as {} from control data", calculatedMean);

        Double calculatedStandardDeviation = calculateStandardDeviation(calculatedMean, controlValues);
        logger.info("Standard deviation calculated as {} from control data", calculatedStandardDeviation);

        // Calculate the upper and lower bounds for our comparisons
        Double calculatedUpperBound = calculateUpperBound(calculatedMean, calculatedStandardDeviation, zScore);
        logger.info("Upper bound calculated as {}", calculatedUpperBound);

        Double calculatedLowerBound = calculateLowerBound(calculatedMean, calculatedStandardDeviation, zScore);
        logger.info("Lower bound calculated as {}", calculatedLowerBound);

        // Perform drift detection
        return detectDrift(calculatedMean, calculatedStandardDeviation, calculatedUpperBound, calculatedLowerBound,
                input);
    }

    protected DriftDetectionResult detectDrift(Double mean, Double standardDeviation, Double upperBound,
            Double lowerBound, DriftData input) {
        DriftDetectionResult result = createNewBaseResult();

        // Validate and convert the data
        List<Double> values = validateAndConvertInput(input);

        // Find the max value
        Double max = Collections.max(values);
        boolean driftDetected = false;
        if (max > upperBound) {

            // Drift has been detected
            driftDetected = true;
            result.addFlaggedValue(max);
            result.getMetadata().put(UPPER_BOUND_CONFIGURATION, upperBound);
        }

        if (!driftDetected) {
            Double min = Collections.min(values);
            if (min < lowerBound) {
                // Drift has been detected
                driftDetected = true;
                result.addFlaggedValue(min);
                result.getMetadata().put(LOWER_BOUND_CONFIGURATION, lowerBound);
            }
        }

        // Add the information about the results
        result.setDriftDiscovered(driftDetected);
        result.setTimestamp(Instant.now());

        // Add metadata about the configurations used for this run, including
        // the mean, standard deviation, and zScore
        if (StringUtils.isNotBlank(input.getName())) {
            result.addDataName(input.getName());
        }
        result.getMetadata().put(MEAN_CONFIGURATION, mean);
        result.getMetadata().put(STANDARD_DEVIATION_CONFIGURATION, standardDeviation);
        result.getMetadata().put(ZSCORE_CONFIGURATION, zScore);

        return result;
    }

    @Override
    public void setConfigurations(Map<String, Object> configurations) {
        super.setConfigurations(configurations);

        if (configurations != null && !configurations.isEmpty()) {
            mean = getDoubleConfiguration(MEAN_CONFIGURATION);
            standardDeviation = getDoubleConfiguration(STANDARD_DEVIATION_CONFIGURATION);
            zScore = getDoubleConfiguration(ZSCORE_CONFIGURATION);
            if (zScore == null) {
                zScore = new Double(DEFAULT_ZSCORE);
            }

            // Calculate the bounds if we have a mean, standard deviation, and
            // limit specified so we don't have to keep re-calculating
            if (mean != null && standardDeviation != null && zScore != null) {
                upperBound = calculateUpperBound(mean, standardDeviation, zScore);
                logger.info("Default upper bound will be set to {}", upperBound);
                lowerBound = calculateLowerBound(mean, standardDeviation, zScore);
                logger.info("Default lower bound will be set to {}", lowerBound);
            }
        }
    }

    protected Double calculateUpperBound(Double mean, Double standardDeviation, Double zScore) {
        Double upperBound = mean + (zScore * standardDeviation);
        upperBound = round(upperBound);
        return upperBound;
    }

    protected Double calculateLowerBound(Double mean, Double standardDeviation, Double zScore) {
        Double lowerBound = mean - (zScore * standardDeviation);
        lowerBound = round(lowerBound);
        return lowerBound;
    }

    @SuppressWarnings("rawtypes")
    protected double[] validateAndConvertControl(DriftData control) {

        // Make sure it's the type we're expecting
        if (!(control instanceof DriftVariables)) {
            throw new DriftDetectionException("Expected DriftVariables to be used for control calculation");
        }

        DriftVariables variables = (DriftVariables) control;
        List list = variables.getVariables();

        // We need some data to use for the control
        if (CollectionUtils.isEmpty(list)) {
            throw new DriftDetectionException("Control data set does not have enough data to be used");
        }

        // The Apache Commons library needs a double[], so we'll just convert it
        // to that format for now.
        double[] values = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            DriftVariable variable = (DriftVariable) list.get(i);
            double value = getValueAsDouble(variable, control.getName());
            values[i] = value;
        }

        return values;
    }

    @SuppressWarnings("rawtypes")
    private List<Double> validateAndConvertInput(DriftData input) {

        if (!(input instanceof DriftVariable || input instanceof DriftVariables)) {
            throw new DriftDetectionException(
                    "Expected DriftVariable or DriftVariables to be used for Standard Deviation Algorithm");
        }

        List<Double> values = new ArrayList<Double>();
        if (input instanceof DriftVariable) {
            Double value = getValueAsDouble((DriftVariable) input, input.getName());
            values.add(value);
        } else {

            // Just create a list of the numbers that were passed in
            DriftVariables variables = (DriftVariables) input;
            List list = variables.getVariables();
            for (int i = 0; i < list.size(); i++) {
                DriftVariable variable = (DriftVariable) list.get(i);
                Double value = getValueAsDouble(variable, input.getName());
                values.add(value);
            }
        }

        return values;

    }

    protected Double calculateMean(double[] controlData) {

        // Don't make this static -- not thread-safe
        Mean meanCalculator = new Mean();
        Double calculatedMean = meanCalculator.evaluate(controlData);
        calculatedMean = round(calculatedMean);
        return calculatedMean;
    }

    protected Double calculateStandardDeviation(double mean, double[] controlData) {

        // Don't make this static -- not thread-safe
        StandardDeviation standardDeviationCalculator = new StandardDeviation();
        double calculatedStandardDeviation = standardDeviationCalculator.evaluate(controlData, mean);
        calculatedStandardDeviation = round(calculatedStandardDeviation);
        return calculatedStandardDeviation;
    }

    protected double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(Double.toString(value));
        BigDecimal rounded = bigDecimal.setScale(NUMBER_DECIMAL_PLACES, RoundingMode.HALF_EVEN);
        return rounded.doubleValue();
    }

    /**
     * Get a {@link Double} for this configuration.
     * 
     * @param key
     * @return DoubleValue
     */
    protected Double getDoubleConfiguration(String key) {
        Object configuration = getConfiguration(key);
        Double value = convertToDouble(configuration);
        if (value != null) {
            logger.info("Setting {} to {} as the default value for this rule", key, value);
        } else {
            logger.warn("Could not convert value {} for configuration {} into a valid number", configuration, key);
        }
        return value;

    }

    protected Double convertToDouble(Object object) {

        // Try converting it to a double
        Double doubleValue = null;
        if (object instanceof Number) {
            doubleValue = ((Number) object).doubleValue();
        } else if (object instanceof String) {
            String text = ((String) object);
            if (StringUtils.isNotBlank(text)) {
                try {
                    doubleValue = Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    logger.error("Could not convert object {} to double because of {}", object, e);
                }
            }
        }
        return doubleValue;
    }

    @SuppressWarnings("rawtypes")
    protected Double getValueAsDouble(DriftVariable variable, String dataset) {
        Object value = variable.getValue();
        Double doubleValue = convertToDouble(value);
        if (doubleValue == null) {
            throw new DriftDetectionException("Value " + value
                    + " could not be converted to a double. Please check values in data set: " + dataset);
        }

        return doubleValue;
    }

    public Double getZScore() {
        return this.zScore;
    }

    public Double getMean() {
        return this.mean;
    }

    public Double getStandardDeviation() {
        return this.standardDeviation;
    }

}
