package com.boozallen.aiops.security.authorization;

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

/**
 * Interface for an AIOps token service client.
 */
public interface AiopsSecureTokenServiceClient {

    /**
     * Authenticates a user.
     * @param username
     * @param password
     * @return a token if authenticated successfully
     */
    public String authenticate(String username, String password);

    /**
     * Convert Json string to jwt.
     * @param token
     * @return a parsed token
     */
    public Jws<Claims> parseToken(String token);

}
