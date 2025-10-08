package com.boozallen.aiops.mda.metamodel.element.python;

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

import com.boozallen.aiops.mda.metamodel.element.Step;

import java.util.HashSet;
import java.util.Set;

/**
 * Extends {@link PythonStep} to specifically model an inference step within a pipeline and
 * facilitate the generation of Python-based inference capabilities.
 */
public class PythonInferenceStep extends PythonStep {

    public PythonInferenceStep(Step stepToDecorate) {
        super(stepToDecorate);
    }

    /**
     * If the inference step is configured to receive a {@link com.boozallen.aiops.mda.metamodel.element.Record}
     * as input, return it wrapped as a {@link PythonRecord}.
     *
     * @return
     */
    public PythonRecord getInboundRecord() {
        return hasInboundRecordType() ? new PythonRecord(getInbound().getRecordType().getRecordType()) : null;
    }

    /**
     * If the inference step is configured to expose a {@link com.boozallen.aiops.mda.metamodel.element.Record}
     * as output, return wrapped as a {@link PythonRecord}
     *
     * @return
     */
    public PythonRecord getOutboundRecord() {
        return hasOutboundRecordType() ? new PythonRecord(getOutbound().getRecordType().getRecordType()) : null;
    }

    public Set<String> getInboundOutboundRecordImports() {
        Set<String> imports = new HashSet<>();
        PythonRecord inboundRecord = getInboundRecord();
        PythonRecord outboundRecord = getOutboundRecord();
        if (inboundRecord != null) {
            imports.addAll(inboundRecord.getBaseImports());
        }
        if (outboundRecord != null) {
            imports.addAll(outboundRecord.getBaseImports());
        }
        return imports;
    }
}
