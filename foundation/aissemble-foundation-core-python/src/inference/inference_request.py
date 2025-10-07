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


class InferenceRequest:
    """Contains details necessary for inference to be invoked"""

    __source_ip_address: str
    __created: int
    __kind: str
    __category: str
    __outcome: str

    def __init__(
        self,
        source_ip_address: str = "",
        created: int = 0,
        kind: str = "",
        category: str = "",
        outcome: str = "",
    ):
        self.source_ip_address = source_ip_address
        self.created = created
        self.kind = kind
        self.category = category
        self.outcome = outcome

    @property
    def source_ip_address(self) -> str:
        return self.__source_ip_address

    @source_ip_address.setter
    def source_ip_address(self, new_value: str):
        self.__source_ip_address = new_value

    @property
    def created(self) -> int:
        return self.__created

    @created.setter
    def created(self, new_value: int):
        self.__created = new_value

    @property
    def kind(self) -> str:
        return self.__kind

    @kind.setter
    def kind(self, new_value: str):
        self.__kind = new_value

    @property
    def category(self) -> str:
        return self.__category

    @category.setter
    def category(self, new_value: str):
        self.__category = new_value

    @property
    def outcome(self) -> str:
        return self.__outcome

    @outcome.setter
    def outcome(self, new_value: str):
        self.__outcome = new_value
