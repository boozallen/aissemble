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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.drift.detection.configuration.ShortHand;
import com.boozallen.drift.detection.policy.json.PolicyInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInput;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputAlgorithm;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputConfiguration;
import com.boozallen.drift.detection.policy.json.rule.PolicyRuleInputTarget;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PolicyTestUtil {

    private static final String[] availableAlgorithms = new String[] { ShortHand.STANDARD_DEVIATION.shortHand,
            "com.different.company.algorithm.CustomDriftAlgorithm" };

    private static final Logger logger = LoggerFactory.getLogger(PolicyTestUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Writes a policy to a file in the directory passed in.
     * 
     * @param policy
     *            policy to be written out
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

        for (int i = 0; i < number; i++) {
            int index = RandomUtils.nextInt(0, availableAlgorithms.length);
            String algorithm = availableAlgorithms[index];

            // Get a good mix of rule configurations written out. Just choose
            // randomly.
            int random = RandomUtils.nextInt(1, 5);
            PolicyRuleInput randomRule = new PolicyRuleInputAlgorithm(algorithm);
            switch (random) {
            case 1:
                randomRule = new PolicyRuleInputConfiguration(algorithm, getRandomConfigs());
                break;
            case 2:
                randomRule = new PolicyRuleInputTarget(algorithm, getRandomTarget());
                break;
            case 3:
                randomRule = new PolicyRuleInput(algorithm, getRandomConfigs(), getRandomTarget());
                break;
            }
            rules.add(randomRule);

        }
        return rules;
    }

    private static String getRandomTarget() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    private static Map<String, Object> getRandomConfigs() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(3));
        map.put(RandomStringUtils.randomAlphabetic(3), RandomUtils.nextInt(1, 100));
        return map;
    }

}
