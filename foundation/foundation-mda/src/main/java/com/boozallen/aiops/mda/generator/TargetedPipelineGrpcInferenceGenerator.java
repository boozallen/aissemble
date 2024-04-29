package com.boozallen.aiops.mda.generator;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aiops.mda.metamodel.element.Step;
import com.boozallen.aiops.mda.metamodel.element.proto.GrpcInferenceStep;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates gRPC and protobuf files that support the inference step of the specific targeted pipeline.  Files
 * will be generated under "src/main/resources/proto/".
 */
public class TargetedPipelineGrpcInferenceGenerator extends BaseTargetedPipelineInferenceGenerator {
    /*--~-~-~~
     * Usages:
     * | Target                               | Template                                   | Generated File                                                                                |
     * |--------------------------------------|--------------------------------------------|-----------------------------------------------------------------------------------------------|
     * | inferenceApiGrpcProto                | inference/inference.api.grpc.proto.vm      | ${artifactIdSnakeCase}/generated/inference/grpc/inference_api.proto                           |
     * | inferencePayloadDefinitionProtoBase  | inference/inference.payload.base.proto.vm  | ${artifactIdSnakeCase}/generated/inference/grpc/generated/inference_payload_definition.proto  |
     */


    @Override
    protected String getOutputSubFolder() {
        return "resources/proto/";
    }

    @Override
    protected List<? extends Step> decorateInferenceSteps(List<Step> inferenceSteps) {
        return inferenceSteps.stream().map(step -> new GrpcInferenceStep(step)).collect(Collectors.toList());
    }
}
