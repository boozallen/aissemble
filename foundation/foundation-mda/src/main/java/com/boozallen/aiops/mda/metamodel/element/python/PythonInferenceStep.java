package com.boozallen.aiops.mda.metamodel.element.python;

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
