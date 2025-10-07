package com.boozallen.aissemble.alerting.core.cdi;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Alerting::Core
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

import java.util.List;

import com.boozallen.aissemble.alerting.core.AlertDeserializer;
import com.boozallen.aissemble.alerting.core.AlertSerializer;
import com.boozallen.aissemble.messaging.core.cdi.MessagingCdiContext;

import com.boozallen.aissemble.alerting.core.AlertProducer;
import com.boozallen.aissemble.alerting.core.AlertProducerApi;
import com.boozallen.aissemble.alerting.core.consumer.AlertLogger;

public class AlertingCdiContext extends MessagingCdiContext {

    @Override
    public List<Class<?>> getCdiClasses() {
        List<Class<?>> classes = super.getCdiClasses();
        classes.add(AlertLogger.class);
        classes.add(AlertProducer.class);
        classes.add(AlertProducerApi.class);
        classes.add(AlertSerializer.class);
        classes.add(AlertDeserializer.class);
        return classes;
    }

}
