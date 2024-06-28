###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from .inference_result import InferenceResult


class InferenceResultBatch:
    """Represents a single result of a batch inference"""

    __row_id_key: str
    __result: InferenceResult

    def __init__(self, row_id_key: str, result: InferenceResult):
        self.row_id_key = row_id_key
        self.result = result

    @property
    def row_id_key(self) -> str:
        return self.__row_id_key

    @row_id_key.setter
    def row_id_key(self, new_value: str):
        self.__row_id_key = new_value

    @property
    def result(self) -> InferenceResult:
        return self.__result

    @result.setter
    def result(self, new_value: InferenceResult):
        self.__result = new_value
