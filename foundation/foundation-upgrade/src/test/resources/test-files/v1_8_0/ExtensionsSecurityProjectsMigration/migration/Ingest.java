package org.boozallen.aissemble;

/*-
 * #%L
 * authzforce1::Pipelines::Spark Pipeline
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */


import javax.enterprise.context.ApplicationScoped;

import com.boozallen.aissemble.security.authorization.AissembleKeyStore;
import com.boozallen.aissemble.security.authorization.AissembleKeycloakSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.AissembleSimpleSecureTokenServiceClient;
import com.boozallen.aissemble.security.authorization.policy.AissembleAttribute;
import com.boozallen.aissemble.security.authorization.policy.AissembleAttributeProvider;
import com.boozallen.aissemble.security.exception.AissembleSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;


/**
 * Performs the business logic for Ingest.
 *
 * Because this class is {@link ApplicationScoped}, exactly one managed singleton instance will exist
 * in any deployment.
 *
 * GENERATED STUB CODE - PLEASE ***DO*** MODIFY
 *
 * Originally generated from: templates/data-delivery-spark/synchronous.processor.impl.java.vm
 */
@ApplicationScoped
public class Ingest extends IngestBase {

    // testing aissemble/security/authorization/ migrations
    private static AiopsKeycloakSecureTokenServiceClient keycloakSecureTokenServiceClient = new AiopsKeycloakSecureTokenServiceClient();
    private static AiopsKeyStore keyStore = new AiopsKeyStore();
    private static AiopsSimpleSecureTokenServiceClient simpleSecureTokenServiceClient = new AiopsSimpleSecureTokenServiceClient();

    // testing aissemble/security/authorization/policy/ migrations
    private static AiopsAttribute attribute = new AiopsAttribute();
    private static AiopsAttributeProvider attributeProvider = new AiopsAttributeProvider();

    // testing aissemble/security/authorization/exception migrations
    private static AiopsSecurityException securityException = new AiopsSecurityException();

    private static final Logger logger = LoggerFactory.getLogger(Ingest.class);

    public Ingest(){
        super("synchronous",getDataActionDescriptiveLabel());
    }

    /**
     * Provides a descriptive label for the action that can be used for logging (e.g., provenance details).
     *
     * @return descriptive label
     */
    private static String getDataActionDescriptiveLabel(){
        // TODO: replace with descriptive label
        return"Ingest";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeStepImpl() {
        // TODO: Add your business logic here for this step!
        logger.error("Implement executeStepImpl(..) or remove this pipeline step!");

    }


}
