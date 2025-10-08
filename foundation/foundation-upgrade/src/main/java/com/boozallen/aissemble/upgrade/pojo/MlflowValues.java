package com.boozallen.aissemble.upgrade.pojo;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
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
import lombok.Data;

/**
 * Java object to represent a aissemble mlflow values yaml file
 */
@Data
public class MlflowValues implements AbstractYamlObject {
    private AissembleMlflow aissembleMlflow;

    /**
     * Function to check whether a {@link MlflowValues} instance contains any config values for mlflow external S3
     * @return {@link Boolean} for whether this instance contains the config
     */
    public Boolean hasExternalS3() {
        return this.aissembleMlflow != null ? this.aissembleMlflow.hasExternalS3(): false;
    }

    @Data
    public static class AissembleMlflow {
        private Mlflow mlflow;

        public Boolean hasExternalS3() {
            return this.mlflow != null ? this.mlflow.hasExternalS3() : false;
        }
    }


    @Data
    public static class Mlflow {
        private ExternalS3 externalS3;

        public Boolean hasExternalS3() {
            return this.externalS3 != null ? true : false;
        }
    }

    @Data
    public static class ExternalS3 {
    }
}
