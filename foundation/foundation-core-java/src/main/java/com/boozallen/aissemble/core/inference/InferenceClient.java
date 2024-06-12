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
import java.util.concurrent.CompletableFuture;

public interface InferenceClient {
    /**
     * Calls inference service with inferenceRequest and returns results.
     * @param inferenceRequest
     * @return Inference results
     */
    CompletableFuture<InferenceResult> infer(InferenceRequest inferenceRequest);

    /**
     * Calls inference service with batch of objects and returns results.
     * @param rowId
     * @param inferenceRequests
     * @return Inference results
     */
    CompletableFuture<InferenceResultsBatch> inferBatch(String rowId, List<InferenceRequest> inferenceRequests);
}
