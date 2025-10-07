###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###
from pydantic import BaseModel
from typing import Dict, List


class TrainingBOM(BaseModel):
    """
    Represent a Bill of Materials for model training.
    """

    class DatasetInfo(BaseModel):
        """
        Represents training dataset information for the Bill of Materials.
        """

        origin: str
        size: int = 0

    class FeatureInfo(BaseModel):
        """
        Represents feature engineering/selection information for the Bill of Materials.
        """

        original_features: List[str] = []
        selected_features: List[str] = []

    class ModelInfo(BaseModel):
        """
        Represents training model information for the Bill of Materials.
        """

        type: str
        architecture: str

    id: str
    start_time: str
    end_time: str
    dataset_info: DatasetInfo
    feature_info: FeatureInfo
    model_info: ModelInfo
    mlflow_params: Dict
    mlflow_metrics: Dict
