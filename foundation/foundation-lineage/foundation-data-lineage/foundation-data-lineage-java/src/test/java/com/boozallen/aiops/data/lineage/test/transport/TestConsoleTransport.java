package com.boozallen.aiops.data.lineage.test.transport;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Java
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.data.lineage.transport.ConsoleTransport;

public class TestConsoleTransport extends ConsoleTransport {
    public static boolean hasReceivedMessage = false;

    @Override
    protected void handleReceivedMessage(String event) {
        super.handleReceivedMessage(event);
        hasReceivedMessage = true;
    }
}
