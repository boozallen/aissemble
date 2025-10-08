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
from .inference_request import InferenceRequest


class InferenceRequestBatch:
    """Contains details necessary for inference to be invoked on a batch"""

    __row_id_key: str
    __data: list[InferenceRequest]

    def __init__(self, row_id_key: str, data: list[InferenceRequest]):
        self.row_id_key = row_id_key
        self.data = data

    @property
    def row_id_key(self) -> str:
        return self.__row_id_key

    @row_id_key.setter
    def row_id_key(self, new_value):
        self.__row_id_key = new_value

    @property
    def data(self) -> list[InferenceRequest]:
        return self.__data

    @data.setter
    def data(self, new_value):
        self.__data = new_value
