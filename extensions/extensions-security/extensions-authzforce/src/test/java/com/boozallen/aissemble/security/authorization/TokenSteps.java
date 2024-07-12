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

import com.boozallen.aissemble.security.authorization.policy.AttributeRequest;
import com.boozallen.aissemble.security.authorization.policy.PolicyRequest;
import io.cucumber.java.After;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TokenSteps {
    private static final String ATTRIBUTE_ID_SEASONS_BATTING_OVER_350 = "urn:aissemble:seasonsBattingOver350";
    private String token;
    private String subject;
    private String audience;

    private static final String PASSWORD = "password";
    private static final String PATH_TO_KEYSTORE = "src/test/resources/truststore/testKeyStore.jks";
    private static final String TEST_ISSUER = "CN=test.com, C=US";

    @After("@jwtToken")
    public void tearDown() {
        token = null;
        subject = null;
        audience = null;
    }

    @When("a token is requested for {string} and {string}")
    public void a_token_is_requested_for_and(String subject, String audience) {
        this.subject = subject;
        this.audience = audience;
        token = JsonWebTokenUtil.createToken(subject, audience, null);
    }
    @Then("the token contains claims for {string}, {string}, and {string}")
    public void the_token_contains_claims_for_and(String expectedSubject, String expectedAudience, String expectedIssuer) {
        Jws<Claims> jwt = JsonWebTokenUtil.parseToken(token);
        assertNotNull("token could not be parsed!", jwt);

        Claims claims = jwt.getBody();
        assertEquals("Unexpected subject!", expectedSubject, claims.getSubject());
        assertEquals("Unexpected audience!", expectedAudience, claims.getAudience());
        assertEquals("Unexpected issuer!", expectedIssuer, claims.getIssuer());
    }

    @When("the following claims:")
    public void a_token_is_requested_for_and_and_the_following_claims(List<PolicyRequest> claims) {
        token = JsonWebTokenUtil.createToken(subject, audience, claims);
    }

    @Then("a claim is returned with the following rule and decision pairings:")
    public void a_claim_is_returned_with_the_following_rule_and_decision_pairings(List<TokenDataInput> expectedResults) throws Throwable {
        Jws<Claims> jwt = JsonWebTokenUtil.parseToken(token);
        assertNotNull("token could not be parsed!", jwt);

        for (TokenDataInput expectedResult : expectedResults) {
            PolicyRequest key = new PolicyRequest();
            key.setName(expectedResult.getName());
            key.setResource(expectedResult.getResource());
            key.setAction(expectedResult.getAction());

            String result = jwt.getBody().get(key.toString(), String.class);

            assertEquals("Unexpected decision encountered! ", expectedResult.getResult(), result);
        }
    }

    @When("a token is requested for {string} with an attribute value claim for seasons batting over {double}")
    public void a_token_is_requested_for_with_an_attribute_value_claim_for_seasons_batting_over(String player, Double double1) {
        Collection<AttributeRequest> attributeClaims = new ArrayList<>();
        AttributeRequest requestedAttribute = new AttributeRequest(ATTRIBUTE_ID_SEASONS_BATTING_OVER_350);
        attributeClaims.add(requestedAttribute);

        token = JsonWebTokenUtil.createToken(player, RandomStringUtils.randomAlphanumeric(5), attributeClaims);
    }

    @Then("a claim is returned with the attributes {string}")
    public void a_claim_is_returned_with_the_attributes(String expectedAttributeValuesRaw) {
        List<String> expectedAttributeValues;
        if(expectedAttributeValuesRaw.length() > 1){
            expectedAttributeValues = Arrays.asList(expectedAttributeValuesRaw.replaceAll("\\s+", "").split(","));
        } else {
            expectedAttributeValues = new ArrayList<>();
        }
        Jws<Claims> jwt = JsonWebTokenUtil.parseToken(token);
        assertNotNull("token could not be parsed!", jwt);

        Claims claims = jwt.getBody();
        String rawAttributeValues = claims.get(ATTRIBUTE_ID_SEASONS_BATTING_OVER_350, String.class);
        String[] foundAttributes = StringUtils.split(rawAttributeValues,",");

        for (int i = 0; i < foundAttributes.length; i++) {
            assertTrue("Returned attribute value was NOT expected!", expectedAttributeValues.contains(foundAttributes[i]));
        }

        if (foundAttributes.length == 0) {
            assertEquals("Expected attributes, but found none!", 0, expectedAttributeValues.size());
        }
    }
}
