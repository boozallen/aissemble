package com.boozallen.aissemble.data.lineage.consumer.subclass;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Consumer Base
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.data.lineage.consumer.MessageHandler;

/**
 * Fake impl class which, when triggered to process a message, will succeed.
 */
public class SuccessfulMessageReceiver extends MessageHandler {}
