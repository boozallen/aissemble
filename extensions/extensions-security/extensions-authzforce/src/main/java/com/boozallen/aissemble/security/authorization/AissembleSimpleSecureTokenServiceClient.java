package com.boozallen.aissemble.security.authorization;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Security::Authzforce::Extensions::Security::Authzforce
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
