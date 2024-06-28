###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
