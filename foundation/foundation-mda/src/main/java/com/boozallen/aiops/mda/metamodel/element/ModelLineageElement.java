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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Represents a model lineage instance.
 */
@JsonPropertyOrder({ "enabled" })
public class ModelLineageElement extends AbstractEnabledElement implements ModelLineage {

}
