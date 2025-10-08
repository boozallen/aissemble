package com.boozallen.aiops.mda.generator.common;

/*-
 * #%L
 * AIOps Foundation::AIOps MDA
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
