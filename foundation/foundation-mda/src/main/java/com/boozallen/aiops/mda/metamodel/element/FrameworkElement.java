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

import com.boozallen.aiops.mda.generator.common.FrameworkEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({ "names" })
public class FrameworkElement implements Framework {

    private FrameworkEnum name;


    /**
     * {@inheritDoc}
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Override
    public FrameworkEnum getName() {
        return name;
    }

    /**
     * Set the name of the framework to enable support
     * @param name the name of the framework
     */
    public void setName(FrameworkEnum name) {
        this.name = name;
    }

}
