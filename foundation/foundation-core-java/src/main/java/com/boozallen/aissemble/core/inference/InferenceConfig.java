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

import org.aeonbits.owner.KrauseningConfig;

@KrauseningConfig.KrauseningSources("inference.properties")
public interface InferenceConfig extends KrauseningConfig {
    @Key("rest-service-url")
    @DefaultValue("localhost")
    String getRestServiceUrl();

    @Key("rest-service-port")
    @DefaultValue("7080")
    int getRestServicePort();

    @Key("grpc-service-url")
    @DefaultValue("localhost")
    String getGrpcServiceUrl();

    @Key("grpc-service-port")
    @DefaultValue("7081")
    int getGrpcServicePort();

    @Key("grpc-thread-count")
    @DefaultValue("10")
    int getGrpcThreadCount();
}
