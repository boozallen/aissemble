###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import jsonpickle
import collections
import aiohttp
from .inference_client import InferenceClient
from .inference_request import InferenceRequest
from .inference_request_batch import InferenceRequestBatch
from .inference_result import InferenceResult
from .inference_result_batch import InferenceResultBatch


class RestInferenceClient(InferenceClient):
    async def infer(self, inference_request: InferenceRequest) -> InferenceResult:
        result = await self.__make_request(inference_request, "/analyze")
        return self.__response_to_inference_result(result)

    async def infer_batch(
        self, inference_request_batch: InferenceRequestBatch
    ) -> list[InferenceResultBatch]:
        result = await self.__make_request(inference_request_batch, "/analyze-batch")
        return self.__response_to_inference_result_batch(result)

    async def __make_request(self, payload, route):
        async with aiohttp.ClientSession() as session:
            url = (
                self._config.rest_service_url()
                + ":"
                + self._config.rest_service_port()
                + route
            )
            payload = self.__to_json_friendly_dict(payload)
            async with session.post(url, json=payload) as response:
                return await response.json()

    def __to_json_friendly_dict(self, target):
        """Accepts object and returns JSON friendly dict stripping class name introduced by @Property decorator"""

        if isinstance(target, str):
            return target
        elif isinstance(target, dict):
            return dict(
                (key, self.__to_json_friendly_dict(val)) for key, val in target.items()
            )
        elif isinstance(target, collections.abc.Iterable):
            return [self.__to_json_friendly_dict(val) for val in target]
        elif hasattr(target, "__dict__"):
            return dict(
                (
                    key.replace(target.__class__.__name__, "").lstrip("_"),
                    self.__to_json_friendly_dict(val),
                )
                for key, val in target.__dict__.items()
            )
        return target

    @staticmethod
    def __response_to_inference_result(response) -> InferenceResult:
        return InferenceResult(response["threat_detected"], response["score"])

    @staticmethod
    def __response_to_inference_result_batch(response) -> list[InferenceResultBatch]:
        return [
            InferenceResultBatch(
                val["row_id"],
                RestInferenceClient.__response_to_inference_result(val["result"]),
            )
            for val in response["results"]
        ]
