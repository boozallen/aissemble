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
import abc

from .inference_config import InferenceConfig
from .inference_request import InferenceRequest
from .inference_request_batch import InferenceRequestBatch
from .inference_result import InferenceResult
from .inference_result_batch import InferenceResultBatch


class InferenceClient(metaclass=abc.ABCMeta):
    """Interface for inference client."""

    _config: InferenceConfig

    def __init__(self):
        self._config = InferenceConfig()

    @classmethod
    def __subclasshook__(cls, subclass):
        return (
            hasattr(subclass, "infer")
            and callable(subclass.infer)
            and hasattr(subclass, "infer_batch")
            and callable(subclass.infer_batch)
        )

    @abc.abstractmethod
    async def infer(self, inference_request: InferenceRequest) -> InferenceResult:
        """Invoke inference"""
        raise NotImplementedError

    @abc.abstractmethod
    async def infer_batch(
        self, inference_request_batch: InferenceRequestBatch
    ) -> list[InferenceResultBatch]:
        """Invoke inference on batch"""
        raise NotImplementedError
