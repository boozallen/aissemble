###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
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
from openlineage.client.run import Job as OpenLineageJob
from .lineage_base import _LineageBase, get_open_lineage_facets
from .data_lineage_config import DataLineageConfig

"""
Contains the general concepts needed to perform a base AIOps Reference Architecture Data Action.
A Data Action is a step within a Data Flow.
"""


class Job(_LineageBase):
    name: str = ""
    _config: DataLineageConfig

    def __init__(self, name: str, facets: dict = None, default_namespace: str = None):
        super().__init__(facets)
        if not isinstance(name, str):
            raise ValueError(
                f"Illegal name object received.  Expected: str.  Received: {type(name)}"
            )
        self._config = DataLineageConfig()
        self.name = name
        self.default_namespace = default_namespace

    def get_open_lineage_job(self):
        return OpenLineageJob(
            name=self.name,
            namespace=self._config.job_namespace(self.name, self.default_namespace),
            facets=get_open_lineage_facets(self.facets),
        )
