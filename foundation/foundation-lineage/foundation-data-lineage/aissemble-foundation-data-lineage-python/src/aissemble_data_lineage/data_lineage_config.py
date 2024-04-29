###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from __future__ import annotations  # added to prevent circular import of Job
import logging
from krausening.properties import PropertyManager


class DataLineageConfig:
    """
    Configurations for data lineage capture
    """

    __legacy_namespace_property = "data.lineage.namespace"

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "data-lineage.properties"
        )
        self.properties.stringPropertyNames()

    def enabled(self):
        """
        Whether to emit data lineage events
        """
        return self.properties.getProperty("data.lineage.enabled", "true")

    def producer(self, job_name=None):
        """
        The producer value to write in data lineage events. Typically the source location for the project producing them
        :param job_name: The name of the job that is creating a given lineage event, used to find an accompanying config
        """
        property_key = f"data.lineage.{job_name}.producer"
        value = self.properties.getProperty(property_key, "")
        if value == "":
            value = self.properties.getProperty(
                "data.lineage.producer", "http://github.com/producer"
            )

        return value

    def schema_url(self):
        """
        The location of the schema defining the structure of captured events
        """
        return self.properties.getProperty(
            "data.lineage.schema.url",
            "https://openlineage.io/spec/1-0-5/OpenLineage.json",
        )

    def job_namespace(self, job_name: str, default_namespace: str = None):
        """
        Job namespace to write events to in a downstream consumer

        :param job_name: name of the job
        :param default_namespace: default namespace of the job
        """
        job_namespace_property = f"data.lineage.{job_name}.namespace"
        property_names = self.properties.stringPropertyNames()

        if job_namespace_property in property_names:
            return self.properties.getProperty(job_namespace_property)
        elif default_namespace is not None:
            return default_namespace
        elif (
            DataLineageConfig.__legacy_namespace_property
            in self.properties.stringPropertyNames()
        ):
            # for legacy support
            return self.properties.getProperty(
                DataLineageConfig.__legacy_namespace_property
            )
        else:
            raise ValueError(f"Could not find property {job_namespace_property}.")

    def dataset_namespace(self, dataset_name):
        """
        Dataset namespace to write events to in a downstream consumer

        :param dataset_name: The name of the dataset
        """
        dataset_namespace_property = f"data.lineage.{dataset_name}.namespace"
        if dataset_namespace_property in self.properties.stringPropertyNames():
            return self.properties.getProperty(dataset_namespace_property)
        elif (
            DataLineageConfig.__legacy_namespace_property
            in self.properties.stringPropertyNames()
        ):
            # for legacy support
            return self.properties.getProperty(
                DataLineageConfig.__legacy_namespace_property
            )
        else:
            raise ValueError(f"Could not find property {dataset_namespace_property}.")

    def transport_console(self):
        """
        The method by which events should be sent out
        """
        return self.properties.getProperty("data.lineage.emission.console", "true")

    def emission_topic(self):
        """
        Topic to emit messages on
        Intentionally undocumented at this time
        """
        return self.properties.getProperty(
            "data.lineage.emission.topic", "lineage-event-out"
        )

    def messaging_enabled(self):
        """
        Option to disable all messaging components, limiting data lineage to exclusively console emission
        """
        return self.properties.getProperty("data.lineage.emission.messaging", "true")
