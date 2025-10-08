package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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

import org.technologybrewery.fermenter.mda.metamodel.element.Metamodel;

/**
 * Defines the contract for a post-action.
 */
public interface PostAction extends Metamodel {

    /**
     * Returns the name of the post-action.
     * 
     * @return post-action name
     */
    String getName();

    /**
     * Returns the type of the post-action.
     * 
     * @return post-action type
     */
    String getType();

    /**
     * Returns the source type of the model to be converted from (for a
     * model-conversion post-action).
     * 
     * @return model source
     */
    String getModelSource();

    /**
     * Returns the target type of the model to be converted to (for a
     * model-conversion post-action).
     * 
     * @return model target
     */
    String getModelTarget();

}
