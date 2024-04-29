###
# #%L
# aiSSEMBLE::Foundation::Model Lineage
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from openlineage.client.facet import (
    DataSourceDatasetFacet,
    Assertion,
    DataQualityAssertionsDatasetFacet,
    OwnershipDatasetFacetOwners,
    OwnershipDatasetFacet,
    SchemaField,
    SchemaDatasetFacet,
    StorageDatasetFacet,
    OwnershipJobFacetOwners,
    OwnershipJobFacet,
    DocumentationJobFacet,
)
from typing import List


from .facets import (
    Hyperparameter,
    HyperparameterRunFacet,
    HardwareComponent,
    HardwareDetailsRunFacet,
    SourceCodeDirectoryJobFacet,
    PerformanceMetric,
    PerformanceMetricRunFacet,
)


class LineageBuilder:
    """
    Provides a mechanism to configure and retrieve OpenLineage facets.
    """

    def __init__(self):
        """
        Constructs a new LineageBuilder object.
        """
        self._data_source = None
        self._data_quality_assertions = None
        self._dataset_ownership = None
        self._schema = None
        self._storage = None
        self._hyperparameter = None
        self._hardware_details = None
        self._documentation = None
        self._job_ownership = None
        self._source_code_directory = None
        self._performance_metric = None

    def set_data_source_dataset_facet(self, name: str, uri: str):
        """
        Configures the source of the dataset.

        :param name: The name of the datasource.
        :param uri: The uri of where the datasource can be retrieved.
        """
        self._data_source = DataSourceDatasetFacet(name, uri)

    def set_data_quality_assertions_facet(self, assertions: List[Assertion]):
        """
        Configures the assertions applied the columns of the dataset.

        :param assertions: List of Assertion objects.
        """
        self._data_quality_assertions = DataQualityAssertionsDatasetFacet(assertions)

    def set_ownership_dataset_facet(self, owners: List[OwnershipDatasetFacetOwners]):
        """
        Configures the owners of the dataset.

        :param owners: List of OwnershipDatasetFacetOwners objects.
        """
        self._dataset_ownership = OwnershipDatasetFacet(owners)

    def set_schema_dataset_facet(self, schemaField: List[SchemaField]):
        """
        Configures the fields of the dataset.

        :param schemaField: List of SchemaField objects.
        """
        self._schema = SchemaDatasetFacet(schemaField)

    def set_storage_dataset_facet(self, storageLayer: str, fileFormat: str):
        """
        Configures the storage format of the dataset.

        :param storageLayer: Storage layer provider with allowed values: iceberg, delta.
        :param fileFormat: File format with allowed values: parquet, orc, avro, json, csv, text, xml.
        """
        self._storage = StorageDatasetFacet(storageLayer, fileFormat)

    def set_hyperparameter_run_facet(self, parameters: List[Hyperparameter]):
        """
        Configures the hyperparameters of the training model.

        :param parameters: List of Hyperparameter objects.
        """
        self._hyperparameter = HyperparameterRunFacet(parameters)

    def set_ownership_job_facet(self, ownership_details: List[OwnershipJobFacetOwners]):
        """
        Set the owner(s) for a training run.

        :param parameters: List of OwnershipJobFacetOwners objects.
        """
        self._job_ownership = OwnershipJobFacet(ownership_details)

    def set_documentation_job_facet(self, documentation: str):
        """
        Set the documentation for a training run.

        :param documentation: documentation of the training run.
        """
        self._documentation = DocumentationJobFacet(documentation)

    def set_hardware_details_run_facet(self, hardware: List[HardwareComponent]):
        """
        Set the hardware details for a training run.

        :param parameter: List of HardwareComponent.
        """
        self._hardware_details = HardwareDetailsRunFacet(hardware)

    def set_source_code_directory_job_facet(
        self, source_type: str, git_url: str, path: str
    ):
        """
        Set the source code directory for a training job.

        :param source_type: Type of the source code directory.
        :param git_url: URL of the Git repository.
        :param path: Path of the directory within the repository.
        """
        self._source_code_directory = SourceCodeDirectoryJobFacet(
            source_type, git_url, path
        )

    def set_performance_metric_run_facet(
        self,
        performance_metric: List[PerformanceMetric],
    ):
        """
        Set the performance of the training model.

        :param parameter: List of PerformanceMetric.
        """
        self._performance_metric = PerformanceMetricRunFacet(performance_metric)

    def get_data_source_dataset_facet(self) -> DataSourceDatasetFacet:
        """
        Returns:
            DataSourceDatasetFacet containing the source of the dataset.
        """
        return (
            DataSourceDatasetFacet("", "")
            if self._data_source is None
            else self._data_source
        )

    def get_data_quality_assertions_facet(self) -> DataQualityAssertionsDatasetFacet:
        """
        Returns:
            DataQualityAssertionsDatasetFacet containing the assertions assigned on columns.
        """
        return (
            DataQualityAssertionsDatasetFacet([])
            if self._data_quality_assertions is None
            else self._data_quality_assertions
        )

    def get_ownership_dataset_facet(self) -> OwnershipDatasetFacet:
        """
        Returns:
            OwnershipDatasetFacet containing the owners of the dataset.
        """
        return (
            OwnershipDatasetFacet([])
            if self._dataset_ownership is None
            else self._dataset_ownership
        )

    def get_schema_dataset_facet(self) -> SchemaDatasetFacet:
        """
        Returns:
            SchemaDatasetFacet containing the fields of the dataset.
        """
        return SchemaDatasetFacet([]) if self._schema is None else self._schema

    def get_storage_dataset_facet(self) -> StorageDatasetFacet:
        """
        Returns:
            StorageDatasetFacet containing the storage format of the dataset.
        """
        return StorageDatasetFacet("", "") if self._storage is None else self._storage

    def get_hyperparameter_run_facet(self) -> HyperparameterRunFacet:
        """
        Returns:
            HyperparameterRunFacet containing the parameters used to train the model.
        """
        return (
            HyperparameterRunFacet([])
            if self._hyperparameter is None
            else self._hyperparameter
        )

    def get_ownership_job_facet(self) -> OwnershipJobFacet:
        """
        Returns:
            OwnershipJobFacet containing the owners of the Job.
        """
        return (
            OwnershipJobFacet([(None, None)])
            if self._job_ownership is None
            else self._job_ownership
        )

    def get_documentation_job_facet(self) -> DocumentationJobFacet:
        """
        Returns:
            DocumentationJobFacet containing the documenation of the Job.
        """
        return (
            DocumentationJobFacet("")
            if self._documentation is None
            else self._documentation
        )

    def get_hardware_details_run_facet(self) -> HardwareDetailsRunFacet:
        """
        Returns:
            HardwareDetailsRunFacet containing the hardware details of the training run.
        """
        return (
            HardwareDetailsRunFacet([HardwareComponent(None, None)])
            if self._hardware_details is None
            else self._hardware_details
        )

    def get_source_code_directory_job_facet(self) -> SourceCodeDirectoryJobFacet:
        """
        Returns:
            SourceCodeDirectoryJobFacet containing the source code directory.
        """
        return (
            SourceCodeDirectoryJobFacet("", "", "")
            if self._source_code_directory is None
            else self._source_code_directory
        )

    def get_performance_metric_run_facet(
        self,
    ) -> PerformanceMetricRunFacet:
        """
        Returns:
            PerformanceMetricRunFacet containing the performance of the training model.
        """
        return (
            PerformanceMetricRunFacet([PerformanceMetric(None, None)])
            if self._performance_metric is None
            else self._performance_metric
        )
