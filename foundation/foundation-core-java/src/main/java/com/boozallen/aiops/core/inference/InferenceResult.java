package com.boozallen.aiops.core.inference;

/*-
 * #%L
 * AIOps Foundation::AIOps Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: Generate in MDA based on record type.
public class InferenceResult {
    @JsonProperty(value = "threat_detected", required = true)
    private Boolean threatDetected;
    @JsonProperty(value = "score", required = true)
    private int score;

    /**
     * Get score
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Set score
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    public Boolean getThreatDetected() {
        return threatDetected;
    }

    public void setThreatDetected(Boolean threatDetected) {
        this.threatDetected = threatDetected;
    }
}
