###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
