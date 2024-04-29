###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
