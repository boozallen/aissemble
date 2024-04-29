package com.boozallen.drift.detection;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.algorithm.DriftAlgorithm;
import com.boozallen.drift.detection.data.DriftData;
import com.boozallen.drift.detection.policy.AlertOptions;
import com.boozallen.drift.detection.policy.DriftDetectionPolicy;
import com.boozallen.drift.detection.policy.PolicyManager;
import com.boozallen.drift.detection.policy.PolicyRule;
import com.boozallen.aissemble.alerting.core.AlertProducer;

public class DriftDetector {

    private static Logger logger = LoggerFactory.getLogger(DriftDetector.class);

    private static PolicyManager policyManager = PolicyManager.getInstance();

    /**
     * Method that invokes drift detection using the policy specified on a
     * default data set.
     * 
     * @param policyIdentifier
     *            the unique identifier of a policy
     * @return driftDetectionResult
     */
    public DriftDetectionResult detect(String policyIdentifier) {
        return detect(policyIdentifier, null, null);
    }

    /**
     * 
     * @param policyIdentifier
     *            the unique identifier of a policy
     * @param input
     *            the data that drift detection will be run on
     * @return driftDetectionResult
     */
    public DriftDetectionResult detect(String policyIdentifier, DriftData input) {
        return detect(policyIdentifier, input, null);
    }

    /**
     * 
     * @param policyIdentifier
     *            the unique identifier of a policy
     * @param input
     *            the data that drift detection will be run on
     * @param control
     *            control data that will be used to calculate metrics for
     *            detecting drift
     * @return driftDetectionResult
     */
    public DriftDetectionResult detect(String policyIdentifier, DriftData input, DriftData control) {

        DriftDetectionPolicy policy = policyManager.getPolicy(policyIdentifier);
        DriftDetectionResult result = null;
        if (policy != null) {
            result = detect(policy, input, control);
        } else {

            // Handle case when policy isn't found. For now, just logging
            logger.error("Could not find policy matching {}", policyIdentifier);
        }
        return result;
    }

    /**
     * Detect drift using the specified policy and the rules that are configured
     * within it.
     * 
     * @param policy
     *            the policy that should be used to calculate drift
     * @param input
     *            the data that drift detection will be run on
     * @param control
     *            control data that will be used to calculate metrics for
     *            detecting drift
     * @return driftDetectionResult
     */
    protected DriftDetectionResult detect(DriftDetectionPolicy policy, DriftData input, DriftData control) {

        // Get the rules for the policy
        List<PolicyRule> rules = policy.getRules();

        // For now, we're just executing one rule. Follow-on ticket to
        // handle multiple rules.
        PolicyRule rule = rules.get(0);
        DriftAlgorithm algorithm = rule.getAlgorithm();

        // Just do all the null checks in one place instead of checking on the
        // individual methods above.
        DriftDetectionResult result = null;
        if (input != null && control != null) {
            logger.info(
                    "Executing drift detection using policy {} on input {} using metrics calculated from control set {}",
                    policy.getIdentifier(), input.getName(), control.getName());
            result = algorithm.calculateDrift(input, control);
        } else if (input != null) {
            logger.info("Executing drift detection using policy {} on input {}", policy.getIdentifier(),
                    input.getName());
            result = algorithm.calculateDrift(input);
        } else {
            logger.info("Executing drift detection using policy {} on default data set", policy.getIdentifier());
            result = algorithm.calculateDrift();
        }

        if (result == null) {
            String error = "Drift detection could not be run with this policy as invoked";
            logger.error(error);
            result = new DriftDetectionResult();
            result.getMetadata().put("error", error);
        }

        addPolicyMetadata(policy, result);

        // Publish the alert
        publishAlert(policy, result);

        return result;
    }

    private void addPolicyMetadata(DriftDetectionPolicy policy, DriftDetectionResult result) {
        result.addPolicyIdentifier(policy.getIdentifier());
        String policyDescription = policy.getDescription();
        if (StringUtils.isNotBlank(policyDescription)) {
            result.getMetadata().put(DriftDetectionResult.POLICY_DESCRIPTION, policy.getDescription());
        }

    }

    protected void publishAlert(DriftDetectionPolicy policy, DriftDetectionResult result) {

        // Publish the results
        if (AlertOptions.ALWAYS.equals(policy.getShouldSendAlert())) {
            publishAlert(result);
        } else if (AlertOptions.ON_DRIFT.equals(policy.getShouldSendAlert()) && result.hasDrift()) {
            publishAlert(result);
        }
    }

    protected void publishAlert(DriftDetectionResult result) {
        AlertProducer alertProducer = CDI.current().select(AlertProducer.class).get();
        alertProducer.sendAlert(result.getStatus(), result.toString());
    }

}
