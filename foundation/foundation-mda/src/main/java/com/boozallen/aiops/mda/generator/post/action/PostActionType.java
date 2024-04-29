package com.boozallen.aiops.mda.generator.post.action;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.PostAction;

/**
 * Enum to represent a post-action type.
 */
public enum PostActionType {

    FREEFORM("freeform"),
    MODEL_CONVERSION("model-conversion")
    ;

    private String value;

    private PostActionType(String value) {
        this.value = value;
    }

    /**
     * Returns the value of this post-action type.
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the type of a post-action.
     * 
     * @param postAction
     *            the post-action whose type to return
     * @return post-action type
     */
    public static PostActionType getPostActionType(PostAction postAction) {
        PostActionType found = null;

        for (PostActionType postActionType : values()) {
            if (postActionType.value.equals(postAction.getType())) {
                found = postActionType;
                break;
            }
        }

        return found;
    }

}
