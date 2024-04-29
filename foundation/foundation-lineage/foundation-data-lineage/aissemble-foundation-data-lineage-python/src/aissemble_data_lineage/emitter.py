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
from typing import Tuple

from aissemble_messaging import MessagingClient, Message
from openlineage.client.serde import Serde
import atexit
from .run import Run
from .job import Job
from .run_event import RunEvent
from .data_lineage_config import DataLineageConfig
from .transport import log_out
from krausening.logging import LogManager
from . import RESDIR


class Emitter:
    """
    Emitter class for sending  Data Lineage information in various forms and mediums.
    """

    _config: DataLineageConfig
    _emitter: MessagingClient | None

    def __init__(self):
        self._config = DataLineageConfig()
        self._emitter = None
        self.logger = LogManager.get_instance().get_logger("Emitter")

        # this is a temporaring way for setting the default microprofile properties
        # future work will allow for overriding of these properties with the messaging client
        self._messaging_properties_path = str(
            RESDIR.joinpath("microprofile-config.properties")
        )

    def build_event_and_emit(self, run: Run, job: Job, status: str):
        """
        Emits RunEvents in the OpenLineage format.

        :param run: A valid Run object describing the data lineage event to be recorded.
        :param job: A valid Job object describing the data lineage event to be recorded.
        :param status: String representing the run event type.
        """
        if any([arg is None for arg in [run, job, status]]):
            raise ValueError(
                "The run, job, and status parameters are required in order to emit RunEvents!"
            )

        run_event = RunEvent(run=run, job=job, event_type=status)

        if self._config.enabled() == "true":
            self.emit_run_event(run_event)

    def build_message_client(self):
        self._emitter = MessagingClient(
            properties_location=self._messaging_properties_path
        )
        atexit.register(self._emitter.shutdown)

    def set_messaging_properties_path(self, messaging_properties_path):
        self._messaging_properties_path = messaging_properties_path
        self.logger.info(
            "Update microprofile-config.properties: {}".format(
                messaging_properties_path
            )
        )

    def emit_run_event(self, run_event: RunEvent):
        """
        Emits RunEvents in the OpenLineage format.

        :param run_event: A valid RunEvent object describing the data lineage event to be recorded.
        """
        if self._config.messaging_enabled() == "true" and self._emitter is None:
            self.build_message_client()

        msg = Message()
        msg.set_payload(Serde.to_json(run_event.get_open_lineage_run_event()))

        if self._config.messaging_enabled() == "true":
            self._emitter.queueForSend(self._config.emission_topic(), msg)

        if self._config.transport_console() == "true":
            log_out(msg)
