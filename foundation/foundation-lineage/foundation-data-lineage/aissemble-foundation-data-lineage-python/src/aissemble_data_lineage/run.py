###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from uuid import UUID
from openlineage.client.run import Run as OpenLineageRun
from .lineage_base import _LineageBase, get_open_lineage_facets

"""
Contains the general concepts needed to perform a base AIOps Reference Architecture Data Action.
A Data Action is a step within a Data Flow.
"""


class Run(_LineageBase):
    run_id: UUID

    def __init__(self, run_id: UUID, facets=None):
        super().__init__(facets)
        if not isinstance(run_id, UUID):
            raise ValueError(
                f"Illegal name object received.  Expected: UUID.  Received: {type(run_id)}"
            )
        self.run_id = run_id

    def get_open_lineage_run(self):
        return OpenLineageRun(
            runId=str(self.run_id), facets=get_open_lineage_facets(self.facets)
        )
