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

import com.boozallen.aissemble.security.authorization.policy.AttributeRequest;
import com.boozallen.aissemble.security.authorization.policy.AttributeValue;
import com.boozallen.aissemble.security.authorization.policy.ClaimType;
import com.boozallen.aissemble.security.authorization.policy.PolicyRequest;
import com.boozallen.aissemble.security.config.SecurityConfiguration;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecision;
import com.boozallen.aissemble.security.authorization.policy.PolicyDecisionPoint;
import com.boozallen.aissemble.security.authorization.policy.AissembleAttributeProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for common JWT operations.
 */
public final class JsonWebTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonWebTokenUtil.class);
    private static final SecurityConfiguration config = KrauseningConfigFactory.create(SecurityConfiguration.class);
    private static final AissembleKeyStore keyStore = new AissembleKeyStore();
    private static final PolicyDecisionPoint pdp = PolicyDecisionPoint.getInstance();
    private static final AissembleAttributeProvider attributeProvider = new AissembleAttributeProvider();

    private JsonWebTokenUtil() {
    }

    public static String createToken(String subject, String audience, Collection<? extends AbstractAuthorizationRequest> ruleClaims) {
        JwtBuilder builder = Jwts.builder();
        builder.id(UUID.randomUUID().toString());
        builder.subject(subject);
        builder.audience().add(audience);
        Date currentTime = new Date();
        builder.notBefore(new Date(currentTime.getTime() - getSkewInMillis()));
        builder.issuedAt(currentTime);
        builder.expiration(new Date(currentTime.getTime() + getExpirationInMillis() + getSkewInMillis()));
        builder.issuer(getIssuer());
        builder.signWith(keyStore.getSigningKey());

        if (ruleClaims != null) {
            for (AbstractAuthorizationRequest ruleClaim : ruleClaims) {
                if (ClaimType.POLICY.equals(ruleClaim.getClaimType())) {
                    PolicyRequest policyClaim = (PolicyRequest) ruleClaim;
                    PolicyDecision decision = pdp.isAuthorized(subject, policyClaim.getResource(),
                            policyClaim.getAction());
                    builder.claim(policyClaim.toString(), decision.toString());
                } else {
                    AttributeRequest attributeClaim = (AttributeRequest) ruleClaim;
                    Collection<AttributeValue<?>> foundAttributes = attributeProvider
                            .getAissembleAttributeByIdAndSubject(attributeClaim.getRequestedAttributeId(), subject);
                    String attributeValue = null;
                    if (foundAttributes != null) {
                        attributeValue = foundAttributes.stream().map(AttributeValue::getValueAsString)
                                .collect(Collectors.joining(","));
                    }
                    builder.claim(attributeClaim.getRequestedAttributeId(), attributeValue);
                }
            }
        }
        builder.issuer(getIssuer());

        return builder.compact();
    }
    
    public static String getUserId(String token) {
        String userId = null;

        if (StringUtils.isNotBlank(token)) {
            Jws<Claims> jwt = JsonWebTokenUtil.parseToken(token);
            Claims claims = jwt.getPayload();
            userId = claims.getSubject();
        }

        return userId;
    }

    /**
     * Decodes a JSON Web Token.
     *
     * @param token
     *            the token to decode
     * @return the decoded token. See JJWT for usage details.
     */
    public static Jws<Claims> parseToken(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith(keyStore.getCertificate().getPublicKey())
                .build();

        Jws<Claims> jwt = parser.parseSignedClaims(token);

        if (logger.isDebugEnabled()) {
            logger.debug("Parsed the following JWT: {}", jwt);
        }

        return jwt;
    }

    private static long getSkewInMillis() {
        return config.getTokenSkewInSeconds() * 1000L;
    }

    private static long getExpirationInMillis() {
        return config.getTokenExpirationInSeconds() * 1000L;
    }

    private static String getIssuer() {
        String issuer = config.getTokenIssuer();
        if (StringUtils.isBlank(issuer)) {
            if (keyStore.getCertificate() != null) {
                issuer = keyStore.getCertificate().getIssuerX500Principal().getName();
            } else {
                issuer = "unspecified";
                logger.warn("No signing certificate found, defaulting to {}", issuer);
            }
        }

        return issuer;
    }
}

