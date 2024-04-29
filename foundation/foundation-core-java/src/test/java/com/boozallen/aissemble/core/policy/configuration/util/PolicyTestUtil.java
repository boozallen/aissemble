package com.boozallen.aissemble.core.policy.configuration.util;

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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.PolicyConfiguration;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;
import com.boozallen.aissemble.core.policy.configuration.policy.Target;
import com.boozallen.aissemble.core.policy.configuration.policy.json.PolicyInput;
import com.boozallen.aissemble.core.policy.configuration.policy.json.rule.PolicyRuleInput;
import com.boozallen.aissemble.core.policy.configuration.policymanager.AbstractPolicyManager;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PolicyTestUtil {

    private static final String[] availableAlgorithms = new String[] { "a.fake.package.and.classname",
            "com.different.company.algorithm.CustomAlgorithm" };

    private static final Logger logger = LoggerFactory.getLogger(PolicyTestUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();
    
    private static PolicyConfiguration configuration = KrauseningConfigFactory.create(PolicyConfiguration.class);

    public static String getRandomAlgorithm() {
        int index = RandomUtils.nextInt(0, availableAlgorithms.length);
        return availableAlgorithms[index];
    }

    public static void writePolicyToDefaultLocation(List<PolicyInput> policies, String fileName) {
        writePoliciesToFile(policies, configuration.getPoliciesLocation(), fileName);
    }

    public static void readPoliciesFromDefaultLocation(AbstractPolicyManager policyManager) {

        // Load the files from the configured path
        Map<String, Policy> policies = policyManager.getPolicies();
        policies.clear();
        policyManager.loadPolicyConfigurations(configuration.getPoliciesLocation());
    }

    /**
     * Writes a policy to a file in the directory passed in.
     * 
     * @param policies
     *            policies to be written out
     * @param directory
     *            the directory to write to
     * @param file
     *            policy filename
     */
    public static void writePoliciesToFile(List<PolicyInput> policies, String directory, String file) {

        // Create the parent directory if need be
        File parentDirectory = new File(directory);
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        // Write the file
        String jsonFile = FilenameUtils.concat(directory, file);
        FileWriter writer = null;
        try {
            writer = new FileWriter(jsonFile);
            String json = mapper.writeValueAsString(policies);
            writer.write(json);
        } catch (Exception e) {
            logger.error("Error writing a file to: {}. Error was: {}", jsonFile, e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("File writer didn't close but honestly, this test is almost over.", e);
                }
            }
        }
    }

    /**
     * Convenience method for wrapping a policy in a list and then writing it to
     * file.
     * 
     * @param policy
     * @param directory
     * @param file
     */
    public static void writePolicyToFile(PolicyInput policy, String directory, String file) {
        writePoliciesToFile(Arrays.asList(policy), directory, file);
    }

    public static PolicyInput getRandomPolicy() {
        PolicyInput policyInput = new PolicyInput(RandomStringUtils.randomAlphabetic(10));
        policyInput.setTarget(getRandomTarget());
        List<PolicyRuleInput> rules = getRandomRules(RandomUtils.nextInt(2, 6));
        policyInput.setRules(rules);
        return policyInput;
    }

    /**
     * Method that creates some random rules. Switches between the different
     * json configs for fun and variety.
     * 
     * @param number
     * @return randomRules
     */
    public static List<PolicyRuleInput> getRandomRules(int number) {
        List<PolicyRuleInput> rules = new ArrayList<PolicyRuleInput>();
        String algorithm = getRandomAlgorithm();
        for (int i = 0; i < number; i++) {

            // Get a good mix of rule configurations written out. Just choose
            // randomly.
            int random = RandomUtils.nextInt(1, 5);
            PolicyRuleInput randomRule = new PolicyRuleInput(algorithm);
            switch (random) {
            case 1:
                randomRule = new PolicyRuleInput(algorithm, getRandomConfigs(), null);
                break;
            case 2:
                randomRule = new PolicyRuleInput(algorithm, null, getRandomConfigs());
                break;
            case 3:
                randomRule = new PolicyRuleInput(algorithm, getRandomConfigs(), getRandomConfigs());
                break;
            }
            rules.add(randomRule);

        }
        return rules;
    }

    private static Target getRandomTarget() {
        Target target = new Target("http://" + RandomStringUtils.randomAlphabetic(5) + ".com",
                RandomStringUtils.randomAlphabetic(5));
        return target;
    }

    private static Map<String, Object> getRandomConfigs() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(3));
        map.put(RandomStringUtils.randomAlphabetic(3), RandomUtils.nextInt(1, 100));
        return map;
    }

}
