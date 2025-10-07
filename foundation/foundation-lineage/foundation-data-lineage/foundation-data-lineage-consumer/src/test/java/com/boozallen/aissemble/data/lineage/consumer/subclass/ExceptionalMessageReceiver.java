package com.boozallen.aissemble.data.lineage.consumer.subclass;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Data Lineage Consumer Base
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

import com.boozallen.aissemble.data.lineage.consumer.MessageHandler;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Fake impl class that, when triggered to process a message, will throw an exception.
 */
public class ExceptionalMessageReceiver extends MessageHandler {
    protected void processRunEvent(Message<String> eventMessage) {
        int x = 1/0;
    }
}
