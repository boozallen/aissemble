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

import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.MetamodelUtils;

import com.boozallen.aiops.mda.generator.post.action.PostActionType;
import com.boozallen.aiops.mda.metamodel.element.util.PythonElementUtils;

/**
 * Provides baseline decorator functionality for {@link PostAction}.
 * 
 * The goal is to make it easier to apply the decorator pattern in various
 * implementations of generators (e.g., Java, python, Docker) so that each
 * concrete decorator only has to decorate those aspects of the class that are
 * needed, not all the pass-through methods that each decorator would otherwise
 * need to implement (that add no real value).
 */
public class BasePostActionDecorator implements PostAction {

    protected PostAction wrapped;

    /**
     * New decorator for {@link PostAction}.
     * 
     * @param postActionToDecorate
     *            instance to decorate
     */
    public BasePostActionDecorator(PostAction postActionToDecorate) {
        MetamodelUtils.validateWrappedInstanceIsNonNull(getClass(), postActionToDecorate);
        wrapped = postActionToDecorate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return wrapped.getFileName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return wrapped.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return wrapped.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelSource() {
        return wrapped.getModelSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getModelTarget() {
        return wrapped.getModelTarget();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate() {
        wrapped.validate();
    }

    /**
     * Returns the capitalized post-action name.
     * 
     * @return capitalized post-action name
     */
    public String getCapitalizedName() {
        return StringUtils.capitalize(getName());
    }

    /**
     * Returns the snake case post-action name.
     * 
     * @return snake case post-action name
     */
    public String getSnakeCaseName() {
        return PythonElementUtils.getSnakeCaseValue(getName());
    }

    /**
     * Whether this post-action is a model-conversion post-action.
     * 
     * @return true if this post-action is a model-conversion post-action
     */
    public boolean isModelConversionPostAction() {
        return getPostActionType() == PostActionType.MODEL_CONVERSION;
    }

    /**
     * Whether this post-action is a freeform post-action
     * 
     * @return true if this post-action is a freeform post-action
     */
    public boolean isFreeformPostAction() {
        return getPostActionType() == PostActionType.FREEFORM;
    }

    private PostActionType getPostActionType() {
        return PostActionType.getPostActionType(this);
    }

}
