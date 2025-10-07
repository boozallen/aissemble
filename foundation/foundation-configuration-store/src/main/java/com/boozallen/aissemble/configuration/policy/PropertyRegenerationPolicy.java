package com.boozallen.aissemble.configuration.policy;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Configuration::Store
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

import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationRule;
import com.boozallen.aissemble.configuration.policy.regeneration.PropertyRegenerationStrategy;
import com.boozallen.aissemble.configuration.store.PropertyKey;
import com.boozallen.aissemble.core.policy.configuration.policy.Policy;

/**
 * Interface used defining policies for determining when to regenerate a given {@link Property}.
 */
public interface PropertyRegenerationPolicy extends Policy {
    
    public PropertyRegenerationStrategy getRegenerationStrategy();

    public List<PropertyRegenerationRule> getPropertyRegenerationRules();

    public List<PropertyKey> getTargetPropertyKeys();
}
