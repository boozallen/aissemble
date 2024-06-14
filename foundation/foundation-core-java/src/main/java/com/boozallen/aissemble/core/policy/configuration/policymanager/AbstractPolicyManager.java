package com.boozallen.aissemble.core.policy.configuration.policymanager;

/*-
 * #%L
 * Policy-Based Configuration::Policy Manager
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.PolicyConfiguration;
import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;
import com.boozallen.aissemble.core.policy.configuration.policy.ConfiguredTarget;
import com.boozallen.aissemble.core.policy.configuration.policy.DefaultPolicy;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * {@link AbstractPolicyManager} is the abstract class that contains the logic
 * for reading and parsing policies and rules from a json configuration file.
 *
 * @author Booz Allen Hamilton
 */
public abstract class AbstractPolicyManager {

    private static final Logger logger = LoggerFactory.getLogger(AbstractPolicyManager.class);

    private static PolicyConfiguration configuration = KrauseningConfigFactory.create(PolicyConfiguration.class);

    private static final String POLICY_LOCATION = "POLICY_LOCATION";

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Policy> policies = new HashMap<>();

    protected AbstractPolicyManager() {
        String policiesLocation = getPoliciesLocation();
        loadPolicyConfigurations(policiesLocation);
    }

    /**
     * Helper method that uses the policy configurations from krausening by
     * default, but can be overridden by a system property POLICY_LOCATION
     *
     * @return policy location
     */
    public String getPoliciesLocation() {
        String policiesLocation = configuration.getPoliciesLocation();
        String systemProperty = StringUtils.trimToNull(System.getProperty(POLICY_LOCATION));

        // Override the configured policy location with system property if it's
        // set
        if (systemProperty != null) {
            policiesLocation = systemProperty;
        }
        return policiesLocation;
    }

    /**
     * Helper method that returns the key of the system property used for overriding
     * the policy configurations location provided from krausening.
     *
     * @return policy location system property key
     */
    public static String getPolicyLocationPropertyKey() {
        return POLICY_LOCATION;
    }

    /**
     * Method that loads policy configurations from json files. Checks that the
     * path exists and sets up default policies if none are loaded.
     *
     * @param policiesLocation
     */
    public void loadPolicyConfigurations(String policiesLocation) {
        long start = System.currentTimeMillis();
        logger.debug("Loading policies from {}...", policiesLocation);

        if (StringUtils.isNotBlank(policiesLocation)) {
            loadJsonFilesInDirectory(policiesLocation);
        }

        if (policies.isEmpty()) {
            logger.error("No policies were configured!");
        }

        long stop = System.currentTimeMillis();
        logger.debug("Loaded {} policy configurations in {} ms", policies.size(), (stop - start));
    }

    private void loadJsonFilesInDirectory(String policyConfigurationsLocation) {

        File locationAsFile = new File(policyConfigurationsLocation);
        if (locationAsFile.exists()) {
            File[] files = locationAsFile.listFiles((FilenameFilter) new SuffixFileFilter(".json"));

            if ((files == null) || (files.length == 0)) {
                logger.warn("No files were found within: {}", locationAsFile.getAbsolutePath());

            } else {
                processFiles(files);
            }
        } else {
            logger.error("Policy file location does not exist. No policies will be loaded...");
        }
    }

    private void processFiles(File[] files) {
        List<PolicyInput> configuredPolicies;
        for (File file : files) {
            try {

                CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                        getDeserializationClass());
                configuredPolicies = objectMapper.readValue(file, javaType);

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

            DefaultPolicy configuredPolicy = createPolicy(policyIdentifier);
            configuredPolicy.setDescription(policy.getDescription());

            // Set the alert options
            configuredPolicy.setAlertOptions(policy.getShouldSendAlert());

            // Set the targets
            List<Target> targets = policy.getAnyTargets();
            configuredPolicy.setTargets(targets);

            // Set any additional configurations
            setAdditionalConfigurations(configuredPolicy, policy);

            List<PolicyRuleInput> ruleInputs = policy.getRules();

            if (CollectionUtils.isNotEmpty(ruleInputs)) {

                for (PolicyRuleInput ruleInput : ruleInputs) {

                    // Add the policy rule if everything loads successfully
                    ConfiguredRule configuredRule = validateAndConfigureRule(ruleInput, targets);
                    if (configuredRule != null) {
                        configuredPolicy.addRule(configuredRule);
                    }
                }
            }

            // Make sure the policy ended up with at least one configured rule,
            // otherwise, don't add it
            List<ConfiguredRule> rules = configuredPolicy.getRules();
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

    protected ConfiguredRule validateAndConfigureRule(PolicyRuleInput ruleInput, List<Target> targets) {
        String className = ruleInput.getClassName();
        Map<String, Object> configurations = ruleInput.getConfigurations();
        Map<String, Object> targetConfigurations = ruleInput.getTargetConfigurations();

        ConfiguredRule configuredRule = null;

        if (StringUtils.isNotBlank(className)) {
            List<ConfiguredTarget> configuredTargets = new ArrayList<>();

            if (targets != null && !targets.isEmpty()) {
                for (Target target: targets) {
                    configuredTargets.add(new ConfiguredTarget(target, targetConfigurations));
                }
            }
            
            configuredRule = new ConfiguredRule(className, configurations, configuredTargets);
        } else {
            logger.warn("Policy rules MUST have a class name. Skipping policy rule with no class name defined...");
        }
        return configuredRule;
    }

    public Policy getPolicy(String policyIdentifier) {
        Policy policy = null;
        if (StringUtils.isNotBlank(policyIdentifier) && policies.containsKey(policyIdentifier)) {
            policy = policies.get(policyIdentifier);
        }
        return policy;
    }

    public Map<String, Policy> getPolicies() {
        return policies;
    }

    /**
     * Method that allows subclasses to override the type reference with a
     * subclass.
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class getDeserializationClass() {
        return PolicyInput.class;
    }

    /**
     * Method that allows subclasses to use an extended type for the policy
     * class.
     *
     * @param policyIdentifier
     * @return
     */
    public DefaultPolicy createPolicy(String policyIdentifier) {
        return new DefaultPolicy(policyIdentifier);
    }

    /**
     * Method that allows subclasses to set any additional configurations while
     * reading a policy.
     */
    public void setAdditionalConfigurations(Policy policy, PolicyInput input) {

    }

}
