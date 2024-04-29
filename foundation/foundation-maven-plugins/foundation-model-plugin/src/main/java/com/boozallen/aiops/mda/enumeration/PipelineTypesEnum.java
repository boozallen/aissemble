package com.boozallen.aiops.mda.enumeration;

/*-
 * #%L
 * AIOps Foundation::aiSSEMBLE MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
