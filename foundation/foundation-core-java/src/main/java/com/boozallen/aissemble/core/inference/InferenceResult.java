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
