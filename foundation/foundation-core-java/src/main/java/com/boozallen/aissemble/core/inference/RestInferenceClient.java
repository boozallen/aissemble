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

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class RestInferenceClient implements InferenceClient {
    private static final Logger logger = LoggerFactory.getLogger(RestInferenceClient.class);
    private static final InferenceConfig config = KrauseningConfigFactory.create(InferenceConfig.class);
    private static final int servicePort = config.getRestServicePort();
    private static final String serviceUrl = config.getRestServiceUrl();

    private WebClient webClient;

    public RestInferenceClient() {
        this.webClient = WebClient.create(Vertx.vertx());
    }

    public RestInferenceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * @inheritDoc
     */
    @Override
    public CompletableFuture<InferenceResult> infer(InferenceRequest inferenceRequest) {
        CompletableFuture<InferenceResult> future = new CompletableFuture<>();

        makeRequest(inferenceRequest, future, InferenceResult.class, "/analyze");

        return future;
    }

    /**
     * @inheritDoc
     */
    @Override
    public CompletableFuture<InferenceResultsBatch> inferBatch(String rowId, List<InferenceRequest> inferenceRequests) {
        CompletableFuture<InferenceResultsBatch> future = new CompletableFuture<>();


        InferenceRequestBatch inferenceRequestBatch = new InferenceRequestBatch();
        inferenceRequestBatch.setRowIdKey(rowId);
        inferenceRequestBatch.setData(inferenceRequests);

        makeRequest(inferenceRequestBatch, future, InferenceResultsBatch.class, "/analyze-batch");
        logger.debug("Queued inference asynchronously!");

        return future;
    }

    private <RETURN_TYPE> void makeRequest(Object payload, CompletableFuture<RETURN_TYPE> future, Class<RETURN_TYPE> returnType, String route) {
        webClient.post(
                servicePort,
                serviceUrl,
                route
        ).sendJson(payload, httpResponseAsyncResult -> {
            if (httpResponseAsyncResult.failed()) {
                logger.error("An error occurred while calling inference service: " + httpResponseAsyncResult.result().bodyAsString());
                future.completeExceptionally(new Exception("An error occurred while calling inference service! See logs for details."));
            } else {
                logger.info("Successfully completed inference asynchronously!");
                try {
                    JsonObject body = httpResponseAsyncResult.result().bodyAsJsonObject();
                    future.complete(body.mapTo(returnType));
                } catch (Exception ex) {
                    logger.error(
                            "The server returned an unexpected response! " +
                                    "Response from server: " + httpResponseAsyncResult.result().bodyAsString());
                    future.completeExceptionally(new Exception("The server returned and unexpected response! See logs for details."));
                }
            }
        });
    }
}
