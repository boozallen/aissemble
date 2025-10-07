package com.boozallen.drift.detection.policy;

/*-
 * #%L
 * Drift Detection::Core
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

/**
 * {@link DriftDetectionPolicy} class maps a rule or set of rules that will be
 * used to calculate drift on an input. The identifier is passed in during drift
 * service invocation. The drift service uses it to find the matching policy and
 * the algorithms and any configurations that should be used for drift
 * calculation.
 * 
 * @author Booz Allen Hamilton
 *
 */
public interface DriftDetectionPolicy {
    
    public AlertOptions getShouldSendAlert();

    public String getIdentifier();

    public String getDescription();

    public List<PolicyRule> getRules();

}
