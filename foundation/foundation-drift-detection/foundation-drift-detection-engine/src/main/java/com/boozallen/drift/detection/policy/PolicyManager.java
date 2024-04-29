package com.boozallen.drift.detection.policy;

/*-
 * #%L
 * Drift Detection::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.algorithm.DriftAlgorithm;
import com.boozallen.drift.detection.configuration.DriftDetectionConfiguration;
import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link PolicyManager} is that class that is used for loading and configuring
 * policies.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class PolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(PolicyManager.class);

    private static DriftDetectionConfiguration configuration = KrauseningConfigFactory
            .create(DriftDetectionConfiguration.class);

    private static String DRIFT_DETECTION_POLICIES = "DRIFT_DETECTION_POLICIES";

    private static PolicyManager instance = null;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, DriftDetectionPolicy> policies = new HashMap<String, DriftDetectionPolicy>();

    private PolicyManager() {
        String driftPoliciesLocation = getDriftPoliciesLocation();
        loadPolicyConfigurations(driftPoliciesLocation);
    }

    /**
     * Helper method that uses the drift configurations from krausening by
     * default, but can be overridden by a system property
     * DRIFT_DETECTION_POLICIES
     * 
     * @return drift detection policy location
     */
    private String getDriftPoliciesLocation() {
        String driftPoliciesLocation = configuration.getDriftPoliciesLocation();
        String systemProperty = StringUtils.trimToNull(System.getProperty(DRIFT_DETECTION_POLICIES));

        // Override the configured policy location with system property if it's
        // set
        if (systemProperty != null) {
            driftPoliciesLocation = systemProperty;
        }
        return driftPoliciesLocation;
    }

    /**
     * Method that loads policy configurations from json files. Checks that the
     * path exists and sets up default policies if none are loaded.
     * 
     * @param driftPoliciesLocation
     */
    public void loadPolicyConfigurations(String driftPoliciesLocation) {
        long start = System.currentTimeMillis();
        logger.debug("Loading drift policies from {}...", driftPoliciesLocation);

        if (StringUtils.isNotBlank(driftPoliciesLocation)) {
            loadJsonFilesInDirectory(driftPoliciesLocation);
        }

        if (policies.isEmpty()) {
            logger.error("No drift detection policies were configured!");
        }

        long stop = System.currentTimeMillis();
        logger.debug("Loaded {} policy configurations in {} ms", policies.size(), (stop - start));
    }

    private void loadJsonFilesInDirectory(String policyConfigurationsLocation) {

        File locationAsFile = new File(policyConfigurationsLocation);
        if (locationAsFile.exists()) {
            File[] files = locationAsFile.listFiles((FilenameFilter) new SuffixFileFilter(".json"));

            if ((files == null) || (files.length == 0)) {
                logger.warn("No files were found within: " + locationAsFile.getAbsolutePath());

            } else {
                List<PolicyInput> configuredPolicies;

                for (File file : files) {
                    try {
                        configuredPolicies = objectMapper.readValue(file, new TypeReference<List<PolicyInput>>() {
                        });

                        if (CollectionUtils.isNotEmpty(configuredPolicies)) {
                            validateAndAddPolicies(configuredPolicies);
                        } else {
                            logger.error("No policies were found in policy configuration file: {}", file.getName());
                        }
                    } catch (IOException e) {
                        logger.error("Could not read policy configuration file: {} because of {}", file.getName(), e);
                    }
                }
            }
        } else {
            logger.error("Policy location does not exist: {}", policyConfigurationsLocation);
        }
    }

    /**
     * Method that adds a list of policies from a file.
     * 
     * @param policies
     */
    protected void validateAndAddPolicies(List<PolicyInput> policies) {
        for (PolicyInput policyInput : policies) {
            validateAndAddPolicy(policyInput);
        }
    }

    /**
     * Method that validates a policy file that's been read in from JSON and
     * sets up the corresponding rules.
     * 
     * @param policy
     */
    protected void validateAndAddPolicy(PolicyInput policy) {

        String policyIdentifier = policy.getIdentifier();

        // Make sure the policy has an identifier, since we need it for policy
        // lookup
        if (StringUtils.isNotBlank(policyIdentifier)) {

            DefaultDriftDetectionPolicy configuredPolicy = new DefaultDriftDetectionPolicy(policyIdentifier);

            // Set the alert options
            configuredPolicy.setShouldSendAlert(policy.getShouldSendAlert());

            List<PolicyRuleInput> ruleInputs = policy.getRules();

            if (CollectionUtils.isNotEmpty(ruleInputs)) {

                for (PolicyRuleInput ruleInput : ruleInputs) {

                    // Add the policy rule if everything loads successfully
                    PolicyRule policyRule = validateAndConfigureRule(ruleInput);
                    if (policyRule != null) {
                        configuredPolicy.addRule(policyRule);
                    }
                }
            }

            // Make sure the policy ended up with at least one configured rule,
            // otherwise, don't add it
            List<PolicyRule> rules = configuredPolicy.getRules();
            if (CollectionUtils.isNotEmpty(rules)) {
                policies.put(policyIdentifier, configuredPolicy);
                logger.debug("Adding policy {} with {} rules configured", configuredPolicy, rules.size());
            } else {
                logger.warn("Policy rules could not be loaded. Skipping policy {}...", policyIdentifier);
            }

        } else {
            logger.warn("Policies MUST have an identifier. Skipping policy with no identifier...");
        }
    }

    protected PolicyRule validateAndConfigureRule(PolicyRuleInput ruleInput) {
        String algorithm = ruleInput.getAlgorithm();
        Map<String, Object> configurations = ruleInput.getConfiguration();
        String target = ruleInput.getTarget();
        PolicyRule policyRule = null;

        if (StringUtils.isNotBlank(algorithm)) {

            DriftAlgorithm driftAlgorithm = getConfiguredDriftAlgorithm(algorithm, target, configurations);
            if (driftAlgorithm != null) {
                policyRule = new DefaultPolicyRule(driftAlgorithm);
            } else {
                logger.warn(
                        "Policy rules MUST have a configured algorithm. Skipping policy rule for algorithm {} due to loading errors...",
                        algorithm);
            }

        } else {
            logger.warn("Skipping rules with no configured algorithms...");
        }
        return policyRule;
    }

    /**
     * Method that uses reflection to load an instance of the
     * {@link DriftAlgorithm} and then configure it, if any configurations exist
     * in the policy file.
     * 
     * @param algorithm
     *            the class name of a drift algorithm, if it exists in the
     *            default package, or the fully qualified class name of a class
     *            that implements {@link DriftAlgorithm}.
     * @param target
     *            the target column/piece of data for the algorithm
     * @param configurations
     *            any other configurations for the algorithm
     * @return driftAlgorithm
     */
    protected DriftAlgorithm getConfiguredDriftAlgorithm(String algorithm, String target,
            Map<String, Object> configurations) {
        Class<DriftAlgorithm> clazz = findAlgorithmClass(algorithm);
        DriftAlgorithm driftAlgorithm = null;
        if (clazz != null) {
            try {
                driftAlgorithm = clazz.newInstance();

                // Configure the target, if there is one
                if (StringUtils.isNotBlank(target)) {
                    Method method = driftAlgorithm.getClass().getMethod("setTarget", String.class);
                    method.invoke(driftAlgorithm, target);
                }

                // Add the algorithm configurations, if there are any
                if (configurations != null) {
                    Method method = driftAlgorithm.getClass().getMethod("setConfigurations", Map.class);
                    method.invoke(driftAlgorithm, configurations);
                }

            } catch (InstantiationException e) {
                logger.warn("Drift Algorithm {} could not be loaded due to {}.", clazz.getName(), e);
            }

            catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                logger.warn("Algorithm customizations for {} will be ignored due to the following error {}", clazz, e);
            }
        } else {
            logger.warn("Could not load drift algorithm for {}. Rule will be ignored...", algorithm);
        }

        return driftAlgorithm;
    }

    private Class<DriftAlgorithm> findAlgorithmClass(String algorithm) {

        Class<DriftAlgorithm> clazz = null;
        String className = algorithm;

        // Check to see if this is short hand for a drift algorithm.
        if (ShortHand.hasClassForShortHand(algorithm)) {
            className = ShortHand.getClassNameForShortHand(algorithm);
        }
        clazz = loadClass(className);
        if (clazz == null) {
            logger.debug("Could not find class for {}, trying with default package instead....", className);
            String packagedClass = configuration.getDefaultAlgorithmPackage() + "." + algorithm;
            clazz = loadClass(packagedClass);
        }

        return clazz;
    }

    @SuppressWarnings("unchecked")
    private Class<DriftAlgorithm> loadClass(String algorithm) {
        Class<DriftAlgorithm> clazz = null;

        // Try loading the class in
        ClassLoader classLoader = PolicyManager.class.getClassLoader();
        try {
            logger.debug("Attempting to load class for {}", algorithm);
            clazz = (Class<DriftAlgorithm>) classLoader.loadClass(algorithm);
        } catch (ClassNotFoundException e) {
            logger.debug("Could not find class for {}", algorithm);
        }
        return clazz;
    }

    public DriftDetectionPolicy getPolicy(String policyIdentifier) {
        DriftDetectionPolicy policy = null;
        if (StringUtils.isNotBlank(policyIdentifier) && policies.containsKey(policyIdentifier)) {
            policy = policies.get(policyIdentifier);
        }
        return policy;
    }

    public Map<String, DriftDetectionPolicy> getPolicies() {
        return policies;
    }

    public static final PolicyManager getInstance() {
        if (instance == null) {
            synchronized (PolicyManager.class) {
                instance = new PolicyManager();
            }
        }
        return instance;
    }

}
