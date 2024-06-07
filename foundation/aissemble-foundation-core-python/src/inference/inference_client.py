###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
