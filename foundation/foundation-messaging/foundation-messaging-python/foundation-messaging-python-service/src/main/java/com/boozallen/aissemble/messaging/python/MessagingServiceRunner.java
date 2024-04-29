package com.boozallen.aissemble.messaging.python;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Messaging::Python::Service
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */
import py4j.GatewayServer;

public class MessagingServiceRunner {
    public static void main(String[] args) {
        GatewayServer gatewayServer = new GatewayServer(MessagingService.getInstance());
        gatewayServer.start();
        System.out.println("Gateway Server Started");
    }
}
