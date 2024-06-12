package com.boozallen.aissemble.messaging.core;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.CDI;

import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boozallen.aissemble.core.cdi.CdiContainer;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CdiConfigurationSteps {

    private static final Logger logger = LoggerFactory.getLogger(CdiConfigurationSteps.class);

    private TestCdiContext context = new TestCdiContext();

    private List<Class<?>> classes = new ArrayList<Class<?>>();

    private WeldContainer weldContainer;

    @After("@cdiConfiguration")
    public void tearDown() {
        classes.clear();
        if (weldContainer != null) {
            weldContainer.close();
            weldContainer = null;
        }
    }

    @Given("I registered a {string} in the CDI configuration")
    public void i_registered_a_in_the_cdi_configuration(String text) {
        Class<?> clazz = loadClass(text);
        context.addClass(clazz);
    }

    @Given("I registered a list of classes in the CDI configuration")
    public void i_registered_a_list_of_classes_in_the_cdi_configuration(List<String> texts) {
        for (String text : texts) {
            context.addClass(loadClass(text));
        }
    }

    @When("the container is initialized")
    public void the_container_is_initialized() {
        weldContainer = CdiContainer.create(Collections.singletonList(context));
    }

    @Then("the {string} can be found in the CDI container")
    public void the_can_be_found_in_the_cdi_container(String text) {
        assertNotNull("Weld container was unexpectedly not initialized", weldContainer);
        Class<?> clazz = loadClass(text);
        Object object = CDI.current().select(clazz).get();
        assertNotNull("Could not find instance of " + text, object);
    }

    @Then("the classes can be found in the CDI container")
    public void the_classes_can_be_found_in_the_cdi_container() {
        for (Class<?> clazz : classes) {
            Object object = CDI.current().select(clazz).get();
            assertNotNull("Could not find instance of " + clazz, object);
        }
    }

    private Class<?> loadClass(String text) {
        Class<?> clazz = null;

        // Try loading the class in
        ClassLoader classLoader = CdiConfigurationSteps.class.getClassLoader();
        try {
            logger.debug("Attempting to load class for {}", text);
            clazz = (Class<?>) classLoader.loadClass(text);
        } catch (ClassNotFoundException e) {
            logger.debug("Could not find class for {}", text);
        }
        return clazz;
    }
}
