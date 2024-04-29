###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import os
from uuid import uuid4, UUID
from aissemble_data_lineage import (
    Run,
    Job,
    Emitter,
    RunEvent,
    Facet,
    Dataset,
    from_open_lineage_facet,
)
from openlineage.client.facet import ParentRunFacet
from typing import List
from krausening.logging import LogManager

from aissemble_data_lineage.lineage_base import _LineageBase


class LineageEventData:
    """
    LineageEventData captures the job facets, run facets, and input/output datasets that can be included in a lineage event
    """

    run_facets: dict[str, Facet] = None
    job_facets: dict[str, Facet] = None
    event_inputs: List[Dataset] = None
    event_outputs: List[Dataset] = None

    def __init__(
        self, run_facets=None, job_facets=None, event_inputs=None, event_outputs=None
    ):
        self.run_facets = run_facets
        self.job_facets = job_facets
        self.event_inputs = event_inputs
        self.event_outputs = event_outputs


class LineageUtil:
    """
    A Lineage Utility class
    """

    logger = LogManager.get_instance().get_logger("LineageUtil")

    def __init__(self):
        pass

    def create_run(
        self,
        run_id: UUID = None,
        facets: dict[str, Facet] = None,
        parent_run_facet: ParentRunFacet = None,
    ) -> Run:
        """
        Creates a Run object with the given run_id, facets and/or parent run facet.
        If there is no run_id given, the new run_id will be generated.

        Returns:
            Run instance created from input arguments.
        """
        if run_id is None:
            run_id = uuid4()

        run = Run(run_id=run_id)

        if facets is not None:
            if parent_run_facet is not None:
                facets.update({"parent": from_open_lineage_facet(parent_run_facet)})

            run.set_facets(facets)
        else:
            if parent_run_facet is not None:
                run.set_facets({"parent": from_open_lineage_facet(parent_run_facet)})

        return run

    def create_job(
        self,
        job_name: str,
        facets: dict[str, Facet] = None,
        default_namespace: str = None,
    ) -> Job:
        """
        Creates a Job object with given job_name and facets.

        Returns:
            Job instance created from input arguments.
        """
        return Job(name=job_name, facets=facets, default_namespace=default_namespace)

    def create_run_event(
        self,
        run: Run,
        job: Job,
        status: str,
        inputs: List[Dataset] = None,
        outputs: List[Dataset] = None,
    ) -> RunEvent:
        """
        Creates the RunEvent object with give Run, Job, status (RunState/EventType), inputs and outputs dataset.

        Returns:
            RunEvent instance created from the input arguments
        """
        event = RunEvent(run, job, status)
        if inputs is not None:
            event.set_inputs(inputs)

        if outputs is not None:
            event.set_outputs(outputs)

        return event

    def create_start_run_event(
        self,
        run_id: UUID = None,
        parent_run_facet: ParentRunFacet = None,
        job_name: str = "",
        default_namespace: str = None,
        event_data: LineageEventData = None,
    ) -> RunEvent:
        """
        Creates the Start RunEvent with given uuid, parent run facet and job name

        Returns:
            RunEvent created from the input arguments
        """
        if event_data is None:
            event_data = LineageEventData()

        return self.create_run_event(
            run=self.create_run(
                run_id=run_id,
                parent_run_facet=parent_run_facet,
                facets=event_data.run_facets,
            ),
            job=self.create_job(
                job_name=job_name,
                default_namespace=default_namespace,
                facets=event_data.job_facets,
            ),
            status="START",
            inputs=event_data.event_inputs,
            outputs=event_data.event_outputs,
        )

    def create_complete_run_event(
        self,
        run_id: UUID = None,
        parent_run_facet: ParentRunFacet = None,
        job_name: str = "",
        default_namespace: str = None,
        event_data: LineageEventData = None,
    ) -> RunEvent:
        """
        Creates the Complete RunEvent with given uuid, parent run facet and job name

        Returns:
            RunEvent created from the input arguments
        """
        if event_data is None:
            event_data = LineageEventData()

        return self.create_run_event(
            run=self.create_run(
                run_id=run_id,
                parent_run_facet=parent_run_facet,
                facets=event_data.run_facets,
            ),
            job=self.create_job(
                job_name=job_name,
                default_namespace=default_namespace,
                facets=event_data.job_facets,
            ),
            status="COMPLETE",
            inputs=event_data.event_inputs,
            outputs=event_data.event_outputs,
        )

    def create_fail_run_event(
        self,
        run_id: UUID = None,
        parent_run_facet: ParentRunFacet = None,
        job_name: str = "",
        default_namespace: str = None,
        event_data: LineageEventData = None,
    ) -> RunEvent:
        """
        Creates the Fail RunEvent with given uuid, parent run facet and job name

        Returns:
            RunEvent created from the input arguments
        """
        if event_data is None:
            event_data = LineageEventData()

        return self.create_run_event(
            run=self.create_run(
                run_id=run_id,
                parent_run_facet=parent_run_facet,
                facets=event_data.run_facets,
            ),
            job=self.create_job(
                job_name=job_name,
                default_namespace=default_namespace,
                facets=event_data.job_facets,
            ),
            status="FAIL",
            inputs=event_data.event_inputs,
            outputs=event_data.event_outputs,
        )

    def record_lineage(self, emitter: Emitter, event: RunEvent):
        """
        Record a given lineage run event and emitted by given emitter
        """
        self.logger.info("Recording lineage data...")
        if os.environ.get("ENABLE_LINEAGE", "false") == "true":
            emitter.emit_run_event(event)
            self.logger.info("Lineage recorded")
        else:
            self.logger.error("Set environment ENABLE_LINEAGE to record lineage data.")
