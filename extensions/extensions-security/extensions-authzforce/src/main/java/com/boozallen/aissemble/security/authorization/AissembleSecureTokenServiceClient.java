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

/**
 * Interface for an aissemble token service client.
 */
public interface AissembleSecureTokenServiceClient {

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
