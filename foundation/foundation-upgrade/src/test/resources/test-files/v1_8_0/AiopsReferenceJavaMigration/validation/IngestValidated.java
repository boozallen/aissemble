package org.boozallen.aissemble;

/*-
 * #%L
 * authzforce::Pipelines::Spark Pipeline
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.filestore.AbstractFileStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF2;
import com.boozallen.aissemble.data.encryption.policy.config.EncryptAlgorithm;
import org.apache.spark.sql.types.DataTypes;

import javax.inject.Inject;
import java.util.Map;
import java.util.HashMap;
import org.boozallen.aissemble.pipeline.PipelineBase;
import com.boozallen.aissemble.security.client.PolicyDecisionPointClient;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicy;
import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicyManager;
import com.boozallen.aissemble.security.exception.AissembleSecurityException;
import com.boozallen.aissemble.security.authorization.models.PDPRequest;
import com.boozallen.aissemble.data.encryption.AiopsEncrypt;
import com.boozallen.aissemble.data.encryption.SimpleAesEncrypt;
import com.boozallen.aissemble.data.encryption.VaultEncrypt;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.types.StructType;
import org.apache.commons.lang.NotImplementedException;

import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

import org.aeonbits.owner.KrauseningConfigFactory;

/**
 * Performs scaffolding synchronous processing for Ingest. Business logic is delegated to the subclass.
 *
 * GENERATED CODE - DO NOT MODIFY (add your customizations in IngestValidated).
 *
 * Generated from: templates/data-delivery-spark/synchronous.processor.base.java.vm
 */
public abstract class IngestValidated extends AbstractPipelineStep {

    private static final Logger logger = LoggerFactory.getLogger(IngestValidated.class);

    protected static final String stepPhase = "IngestValidated";

    protected IngestValidated(String subject, String action) {
        super(subject, action);

        // Register the encryption UDF
        sparkSession.sqlContext().udf().register("encryptUDF", encryptUDF(), DataTypes.StringType);
    }

    public void executeStep() {
        long start = System.currentTimeMillis();
        logger.debug("START: step execution...");

        // TODO: add authorization check here
        try {

            executeStepImpl();

            long stop = System.currentTimeMillis();
            long ms = stop - start;

            logger.debug("COMPLETE: step execution completed in {}ms", (stop - start));

        } catch (Exception e) {
            logger.error("Step failed to complete", e);
            throw e;
        }

    }

    /**
     * This method performs the business logic of this step.
     *
     */
    protected abstract void executeStepImpl();

    /***
     * Calls the Policy Decision Point with the jwt
     *
     * @param jwt
     *            the authenticated token
     * @return a policy decision
     */
    protected String getAuthorization(String jwt) {
        PolicyDecisionPointClient policyDecisionPointClient = new PolicyDecisionPointClient();

        PDPRequest pdpRequest = new PDPRequest();
        pdpRequest.setJwt(jwt);
        pdpRequest.setResource("");
        pdpRequest.setAction("data-access");

        return policyDecisionPointClient.getPolicyDecision(pdpRequest);
    }

    /**
     * Spark User Defined Function for running encryption on columns. Note: must be registered with the spark session.
     *
     * @return The cipher text
     */
    protected UDF2<String, String, String> encryptUDF () {
        return (plainText, encryptAlgorithm) -> {
            if (plainText != null) {
                // Default algorithm is AES
                AiopsEncrypt aiopsEncrypt = new SimpleAesEncrypt();

                if (encryptAlgorithm.equals("VAULT_ENCRYPT")) {
                    aiopsEncrypt = new VaultEncrypt();
                }

                return aiopsEncrypt.encryptValue(plainText);
            } else {
                return "";
            }
        };
    }

}
