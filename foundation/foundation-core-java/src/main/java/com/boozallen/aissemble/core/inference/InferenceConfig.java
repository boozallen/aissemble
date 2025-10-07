package com.boozallen.aissemble.core.inference;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
