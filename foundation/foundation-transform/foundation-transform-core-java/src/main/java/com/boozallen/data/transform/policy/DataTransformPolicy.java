package com.boozallen.data.transform.policy;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Transform::Java
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

import org.technologybrewery.mash.BaseMediationManager;

import com.boozallen.aissemble.core.policy.configuration.policy.Policy;

/**
 * {@link DataTransformPolicy} class represents the contract for a data
 * transform policy.
 * 
 * @author Booz Allen Hamilton
 * 
 */
public interface DataTransformPolicy extends Policy {

    /**
     * Returns the mediation manager instance for this policy.
     * 
     * @return mediation manager instance
     */
    public BaseMediationManager getMediationManager();

}
