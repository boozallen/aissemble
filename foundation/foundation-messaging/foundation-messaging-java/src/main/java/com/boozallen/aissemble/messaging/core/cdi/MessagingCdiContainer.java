package com.boozallen.aissemble.messaging.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Messaging::Messaging
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
