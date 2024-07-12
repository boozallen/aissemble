package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for authenticating via a simple JWT token.
 */
public class AissembleSimpleSecureTokenServiceClient implements AissembleSecureTokenServiceClient {

    static final Logger logger = LoggerFactory.getLogger(AissembleSimpleSecureTokenServiceClient.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String authenticate(String username, String password) {
        logger.info("Generating simple jwt token");
        return JsonWebTokenUtil.createToken(username, "audience", null);
    }

    /**
     * {@inheritDoc}
     */
    @Override public Jws<Claims> parseToken(String token) {
        return JsonWebTokenUtil.parseToken(token);
    }

}
