package com.boozallen.aissemble.messaging.example;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ACdiComponent {

    private static final Logger logger = LoggerFactory.getLogger(ACdiComponent.class);

    public void iDoSomethingWithCdi() {
        logger.warn("I'm doing stuff!");
    }

}
