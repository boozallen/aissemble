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

import com.boozallen.aissemble.security.config.SecurityConfiguration;
import com.boozallen.aissemble.security.exception.AissembleSecurityException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client for authenticating via KeyCloak.
 */
public class AissembleKeycloakSecureTokenServiceClient implements AissembleSecureTokenServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AissembleKeycloakSecureTokenServiceClient.class);

    private static final SecurityConfiguration configuration = KrauseningConfigFactory.create(SecurityConfiguration.class);

    // These values may be moved into a secrets-as-a-service store in the future
    private static final boolean AUTHENTICATION_ENABLED = configuration.authenticationEnabled();
    private static final String CLIENT_SECRET = configuration.authenticationClientSecret();
    private static final String SERVER_URL = configuration.authenticationHost();
    private static final String REALM = configuration.authenticationRealm();
    private static final String CLIENT_ID = configuration.authenticationClientId();

    /**
     * {@inheritDoc}
     */
    @Override
    public String authenticate(String username, String password) {
        String token = null;
        
        if(AUTHENTICATION_ENABLED) {
            AccessTokenResponse keycloakToken = getKeycloakToken(username, password);
            if (keycloakToken != null) {
                token = keycloakToken.getToken();
            } else {
                String error = "Authentication is enabled, but user is not authenticated!";
                logger.error(error);
                throw new AissembleSecurityException(error);
            }
        } else {
            // TODO: Once authentication and authorization are fully implemented we need to decide what to do if
            // the configuration file sets authorization to false.  i.e. anonymous access(?)
            // Generic JWT token for now
            token = configuration.getGenericJWTToken();
        }

        return token;
    }

    @Override public Jws<Claims> parseToken(String token) {
        throw new NotImplementedException();
    }

    private AccessTokenResponse getKeycloakToken(String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .password(password)
                .username(username)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(20).build())
                .build();
        
        return keycloak.tokenManager().getAccessToken();
    }

}
