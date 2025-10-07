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
