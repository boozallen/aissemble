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

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InferenceRequestBatch {
   @JsonProperty("row_id_key")
   private String rowIdKey;
   private List<InferenceRequest> data;

   /**
    * Get row id key
    * @return
    */
   public String getRowIdKey() {
      return rowIdKey;
   }

   /**
    * Set row id key
    * @param rowIdKey
    */
   public void setRowIdKey(String rowIdKey) {
      this.rowIdKey = rowIdKey;
   }

   /**
    * Get data
    * @return
    */
   public List<InferenceRequest> getData() {
      return data;
   }

   /**
    * Set data
    * @param data
    */
   public void setData(List<InferenceRequest> data) {
      this.data = data;
   }


}
