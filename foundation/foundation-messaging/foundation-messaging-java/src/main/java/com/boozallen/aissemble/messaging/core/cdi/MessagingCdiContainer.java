package com.boozallen.aissemble.messaging.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Messaging
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.core.cdi.CdiContainer;

/**
 * The {@link MessagingCdiContainer} holds all the contexts necessary to use
 * reactive messaging.
 * 
 * @author Booz Allen Hamilton
 *
 */
public class MessagingCdiContainer extends CdiContainer {

    public MessagingCdiContainer() {
        super(new MessagingCdiContext());
    }

}
