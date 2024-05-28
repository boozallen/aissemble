package com.boozallen.aissemble.core.inference;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;

/**
 * Represents a collection of results from batch inference.
 */
public class InferenceResultsBatch {
    /**
     * Get results
     * @return
     */
    public List<InferenceResultBatch> getResults() {
        return results;
    }

    /**
     * Set results
     * @param results
     */
    public void setResults(List<InferenceResultBatch> results) {
        this.results = results;
    }

    List<InferenceResultBatch> results;
}
