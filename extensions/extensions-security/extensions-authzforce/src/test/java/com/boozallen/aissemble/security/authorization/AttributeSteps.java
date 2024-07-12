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

import com.boozallen.aissemble.security.authorization.policy.AissembleAttribute;
import com.boozallen.aissemble.security.authorization.policy.AissembleAttributeUtils;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeDesignatorType;
import org.apache.commons.lang3.RandomStringUtils;

import static org.junit.Assert.assertEquals;

public class AttributeSteps {
    private AissembleAttribute aissembleAttribute;
    private AttributeDesignatorType xacmlAttribute;

    @Before
    public void setUp() {
        aissembleAttribute = null;
        xacmlAttribute = null;
    }

    @Given("an attribute with aissemble type {string}")
    public void an_attribute_with_aissemble_type(String aissembleType) {
        aissembleAttribute = new AissembleAttribute();
        aissembleAttribute.setType(aissembleType);

        aissembleAttribute.setId(RandomStringUtils.randomAlphanumeric(10));
        aissembleAttribute.setCategory("action");
        aissembleAttribute.setRequired(false);
    }

    @When("the attribute is read")
    public void the_attribute_is_read() {
        xacmlAttribute = AissembleAttributeUtils.translateAttributeToXacmlFormat(aissembleAttribute);
    }

    @Then("the fully qualified type {string} is returned")
    public void the_fully_qualified_type_is_returned(String fullyQualifiedType) {
        assertEquals(fullyQualifiedType, xacmlAttribute.getDataType());
    }
}

