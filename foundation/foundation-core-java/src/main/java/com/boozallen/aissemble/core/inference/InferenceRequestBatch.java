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
