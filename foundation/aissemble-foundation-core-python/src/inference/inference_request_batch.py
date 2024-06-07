###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
