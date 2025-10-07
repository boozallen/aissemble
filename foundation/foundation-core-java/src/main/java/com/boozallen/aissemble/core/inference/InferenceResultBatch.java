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

/**
 * Represents a single result of a batch inference.
 */
public class InferenceResultBatch {
   @JsonProperty("row_id")
   private String rowId;
   private InferenceResult result;

   /**
    * Get row id key
    * @return
    */
   public String getRowId() {
      return rowId;
   }

   /**
    * Set row id key
    * @param rowId
    */
   public void setRowId(String rowId) {
      this.rowId = rowId;
   }

   /**
    * Get result
    * @return
    */
   public InferenceResult getResult() {
      return result;
   }

   /**
    * Set result
    * @param result
    */
   public void SetResult(InferenceResult result) {
      this.result = result;
   }
}
