###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from datetime import datetime
from typing import List

from . import Dataset
from .run import Run
from .job import Job
from openlineage.client.run import RunEvent as OpenLineageRunEvent, RunState
from .data_lineage_config import DataLineageConfig

"""
Contains the general concepts needed to perform a base AIOps Reference Architecture Data Action.
A Data Action is a step within a Data Flow.
"""


class RunEvent:
    event_type: str
    event_time: datetime
    run: Run
    job: Job
    inputs: List[Dataset]
    outputs: List[Dataset]
    producer: str
    schema_URL: str

    _config: DataLineageConfig

    def __init__(self, run: Run, job: Job, event_type: str):
        self._config = DataLineageConfig()
        self.schema_URL = self._config.schema_url()
        self.event_time = datetime.utcnow()
        self.producer = self._config.producer(job_name=job.name)
        self.run = run
        self.job = job
        self.inputs = []
        self.outputs = []
        self.event_type = event_type

    def set_inputs(self, inputs: List[Dataset]) -> None:
        """
        :param inputs: List of Datasets to be added to this RunEvent's inputs collection
        """
        self.inputs = inputs

    def get_inputs(self) -> List[Dataset]:
        """
        :return: A copy of this RunEvent's current inputs collection
        """
        return self.inputs.copy()

    def add_input(self, dataset: Dataset) -> None:
        """
        Appends a dataset to the input dataset list
        :param dataset: Dataset to be added to this RunEvent's inputs collection
        """
        self.inputs.append(dataset)

    def set_outputs(self, outputs: List[Dataset]) -> None:
        """
        :param outputs: List of Datasets to be added to this RunEvent's outputs collection
        """
        self.outputs = outputs

    def get_outputs(self) -> List[Dataset]:
        """
        :return: A copy of this RunEvent's current outputs collection
        """
        return self.outputs.copy()

    def add_output(self, dataset: Dataset) -> None:
        """
        Appends a dataset to the output dataset list
        :param dataset: Dataset to be added to this RunEvent's outputs collection
        """
        self.outputs.append(dataset)

    def get_open_lineage_run_event(self) -> OpenLineageRunEvent:
        """
        Converts this aiSSEMBLE RunEvent to its OpenLineage form.
        :return:
        """

        event = OpenLineageRunEvent(
            eventType=RunState[self.event_type],
            eventTime=self.event_time.isoformat(timespec="milliseconds") + "Z",
            run=self.run.get_open_lineage_run(),
            job=self.job.get_open_lineage_job(),
            inputs=[dataset.get_open_lineage_dataset() for dataset in self.inputs],
            outputs=[dataset.get_open_lineage_dataset() for dataset in self.outputs],
            producer=self.producer,
            # TODO: Add schemaURL argument after updating to OpenLineage Client release >0.27.2
            # schemaURL=self.schema_URL
        )
        return event
