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
