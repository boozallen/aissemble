###
# #%L
# aiSSEMBLE Foundation::aiSSEMBLE Core (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from abc import ABC, abstractmethod
from typing import Dict, List
from .metadata_model import MetadataModel


class MetadataAPI(ABC):
    """
    API for a metadata service.
    """

    @abstractmethod
    def create_metadata(self, metadata: MetadataModel) -> None:
        """
        Method to create metadata.
        """
        pass

    @abstractmethod
    def get_metadata(self, search_params: Dict[str, any]) -> List[MetadataModel]:
        """
        Method to get metadata from search criteria.
        """
        pass
