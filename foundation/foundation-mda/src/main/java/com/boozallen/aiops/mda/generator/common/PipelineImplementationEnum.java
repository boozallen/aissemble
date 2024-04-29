package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum PipelineImplementationEnum {
	DATA_DELIVERY_SPARK("data-delivery-spark"),
    DATA_DELIVERY_PYSPARK("data-delivery-pyspark"),
	MACHINE_LEARNING("machine-learning-mlflow");

    private String pipelineImplementation;

    private PipelineImplementationEnum(String pipelineImplementation) {
        this.pipelineImplementation = pipelineImplementation;
    }

    public boolean equalsIgnoreCase(String compareType){
        return this.pipelineImplementation.equalsIgnoreCase(compareType);
    }
    
    public static boolean isValidImplementation(String implementation) {
    	PipelineImplementationEnum[] vals = values();
    	for (PipelineImplementationEnum enumeration : vals) {
    		if (enumeration.equalsIgnoreCase(implementation)) {
    			return true;
    		}
    	}
    	return false;
    }

    public static String getPossibleValues() {
        return Stream.of(values())
                .map(type -> type.pipelineImplementation)
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
