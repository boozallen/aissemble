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

/**
 * Defines the contract for how provenance is configured.
 */
public interface Provenance extends AbstractEnabled {

    /**
     * Name of the resource being operated on.
     * @return the name of the resource
     */
    String getResource();

    /**
     * Name of the subject responsible for the action.
     * @return the name of the subject
     */
    String getSubject();

    /**
     * Name of the action being taken.
     * @return name of the action
     */
    String getAction();

}
