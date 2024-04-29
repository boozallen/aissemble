package com.boozallen.data.transform.spark;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Transform::Spark::Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

import com.boozallen.data.transform.DataTransformException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.Dataset;
import org.technologybrewery.mash.MediationContext;
import org.technologybrewery.mash.Mediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.policy.configuration.configuredrule.ConfiguredRule;
import com.boozallen.data.transform.policy.DataTransformPolicy;
import com.boozallen.data.transform.policy.DataTransformPolicyManager;

/**
 * {@link DataTransformer} class represents the rule engine for looking up a
 * specified data transform policy, invoking the corresponding transformation
 * rules, and then processing the results from rule invocation.
 * 
 * @author Booz Allen Hamilton
 * 
 */
public class DataTransformer {

    private static final Logger logger = LoggerFactory.getLogger(DataTransformer.class);
    private static final DataTransformPolicyManager policyManager = DataTransformPolicyManager.getInstance();

    /**
     * Applies a data transform policy on a dataset.
     * 
     * @param policyIdentifier
     *            the identifier of the policy to apply
     * @param dataset
     *            the dataset to apply the policy on
     * @return the dataset after applying the policy
     */
    @SuppressWarnings("rawtypes")
    public Dataset transform(String policyIdentifier, Dataset dataset) {
        DataTransformPolicy policy = (DataTransformPolicy) policyManager.getPolicy(policyIdentifier);
        List<ConfiguredRule> rules = policy != null ? policy.getRules() : null;

        if (policy == null) {
            throw new DataTransformException(
                    "No data transform policy found for identifier '" + policyIdentifier + "'");
        }

        if (CollectionUtils.isEmpty(rules)) {
            throw new DataTransformException("No rules found in data transform policy '" + policyIdentifier + "'");
        }

        if (rules.size() > 1) {
            throw new DataTransformException("Data transform policy '" + policyIdentifier
                    + "' contains multiple rules, which is currently not supported");
        }

        return invokeMediator(policy, rules.get(0), dataset);
    }

    @SuppressWarnings("rawtypes")
    private Dataset invokeMediator(DataTransformPolicy policy, ConfiguredRule rule, Dataset dataset) {
        String inputType = (String) rule.getConfiguration("inputType");
        String outputType = (String) rule.getConfiguration("outputType");

        if (StringUtils.isBlank(inputType) || StringUtils.isBlank(outputType)) {
            throw new DataTransformException(
                    "Rule inputType and/or outputType configuration is missing in data transform policy '"
                            + policy.getIdentifier() + "'");
        }

        // look up the mediator to invoke
        MediationContext mediationContext = new MediationContext(inputType, outputType);
        Mediator mediator = policy.getMediationManager().getMediator(mediationContext);

        logger.debug("Invoking mediator {}", mediator.getClass().getName());

        return (Dataset) mediator.mediate(dataset);
    }

}
