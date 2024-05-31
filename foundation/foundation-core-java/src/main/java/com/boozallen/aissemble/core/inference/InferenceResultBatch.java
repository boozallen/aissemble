package com.boozallen.aissemble.core.inference;

/*-
 * #%L
 * aiSSEMBLE Foundation::aiSSEMBLE Core
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
