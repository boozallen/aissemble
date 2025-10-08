package com.boozallen.aiops.mda.enumeration;

/*-
 * #%L
 * AIOps Foundation::aiSSEMBLE MDA
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

import java.util.HashMap;
import java.util.Map;

public enum PipelineTypesEnum {
    
    DATA_DELIVERY_PYSPARK_TYPE("data-delivery-pyspark", 1),
    DATA_DELIVERY_SPARK_TYPE("data-delivery-spark", 2),
    MACHINE_LEARNING_TYPE("machine-learning-pipeline", 3),

    DATA_FLOW_IMPLEMENTATION("data-flow", -1),
    MACHINE_LEARNING_IMPLEMENTATION("machine-learning", -1);

    private final String name;
    private final int order;

    private static final Map<String, PipelineTypesEnum> nameLookup = new HashMap<String, PipelineTypesEnum>();
    private static final Map<Integer, PipelineTypesEnum> numLookup = new HashMap<Integer, PipelineTypesEnum>();

    static {
        for (PipelineTypesEnum e : PipelineTypesEnum.values()) {
            nameLookup.put(e.getName(), e);
            numLookup.put(e.getOrder(), e);
        }
    }

    PipelineTypesEnum(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public static PipelineTypesEnum getByName(String name) {
        return nameLookup.get(name);
    }

    public static PipelineTypesEnum getByOrder(int order) {
        return numLookup.get(order);
    }
}
