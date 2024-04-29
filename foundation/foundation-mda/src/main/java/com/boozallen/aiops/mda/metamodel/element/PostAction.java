package com.boozallen.aiops.mda.metamodel.element;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
