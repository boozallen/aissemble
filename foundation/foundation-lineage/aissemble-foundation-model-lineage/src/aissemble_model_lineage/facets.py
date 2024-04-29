###
# #%L
# aiSSEMBLE::Foundation::Model Lineage
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import attr
from openlineage.client.facet import BaseFacet
from typing import List

import os
from importlib.metadata import version

aissemble_version = version("aissemble-foundation-model-lineage")

# Replaces .dev83197842 (random numbers) with -SNAPSHOT
if ".dev" in aissemble_version:
    aissemble_version_split = aissemble_version.split(".")
    aissemble_version = ".".join(aissemble_version_split[:-1]) + "-SNAPSHOT"


# """
# Represents basic Dataset entities in the context of Data Lineage.
# """
@attr.s
class SourceCodeDirectoryJobFacet(BaseFacet):
    """
    SourceCodeDirectoryJobFacet extends the OpenLineage BaseFacet class and should be
    utilized to store the pipeline step's directory.
    """

    sourceType: str = attr.ib()
    url: str = attr.ib()
    path: str = attr.ib()
    _additional_skip_redact: ["sourceType", "url", "path"]

    def __init__(self, sourceType, url, path):
        super().__init__()
        self.sourceType = sourceType
        self.url = url
        self.path = path

    @staticmethod
    def _get_schema() -> str:
        return f"https://boozallen.github.io/aissemble/openlineage/schemas/{aissemble_version}/SourceCodeDirectoryJobFacet.json"


@attr.s
class MLflowRunFacet(BaseFacet):
    """
    Captures information about MLflow for model lineage purposes.
    """

    runUUID: str = attr.ib()
    _additional_skip_redact: ["runUUID"]

    def __init__(self, runUUID):
        super().__init__()
        self.runUUID = runUUID

    @staticmethod
    def _get_schema() -> str:
        return f"https://boozallen.github.io/aissemble/openlineage/schemas/{aissemble_version}/MLflowRunFacet.json"


@attr.s
class HardwareComponent:
    """
    Class to store the component and specification of a hardware used for training.
    """

    component: str = attr.ib()
    spec: str = attr.ib()

    def __init__(self, component, spec):
        self.component = component
        self.spec = spec


@attr.s
class HardwareDetailsRunFacet(BaseFacet):
    """
    Captures information about the hardware used during training for model lineage purposes.
    """

    hardware: List[HardwareComponent] = attr.ib(factory=dict)
    _additional_skip_redact: ["hardware"]

    def __init__(self, hardware):
        super().__init__()
        self.hardware = hardware

    @staticmethod
    def _get_schema() -> str:
        return f"https://boozallen.github.io/aissemble/openlineage/schemas/{aissemble_version}/HardwareDetailsRunFacet.json"


@attr.s
class Hyperparameter:
    parameter: str = attr.ib()
    value: any = attr.ib()

    def __init__(self, parameter, value):
        self.parameter = parameter
        self.value = value


@attr.s
class HyperparameterRunFacet(BaseFacet):
    """
    HyperparameterRunFacet extends the OpenLineage BaseFacet class and should be
    utilized to store the parameters of the trained model.
    """

    hyperparameters: List[Hyperparameter] = attr.ib(factory=dict)
    _additional_skip_redact: ["hyperparameters"]

    def __init__(self, hyperparameters):
        super().__init__()
        self.hyperparameters = hyperparameters

    @staticmethod
    def _get_schema() -> str:
        return f"https://boozallen.github.io/aissemble/openlineage/schemas/{aissemble_version}/HyperparameterRunFacet.json"


@attr.s
class PerformanceMetric:
    metric: str = attr.ib()
    value: any = attr.ib()

    def __init__(self, metric, value):
        self.metric = metric
        self.value = value


@attr.s
class PerformanceMetricRunFacet(BaseFacet):
    """
    PerformanceMetricRunFacet extends the OpenLineage BaseFacet class and should be
    utilized to store the performance of the trained model.
    """

    performance: List[PerformanceMetric] = attr.ib(factory=dict)
    _additional_skip_redact: ["performance"]

    def __init__(self, performance):
        super().__init__()
        self.performance = performance

    @staticmethod
    def _get_schema() -> str:
        return f"https://boozallen.github.io/aissemble/openlineage/schemas/{aissemble_version}/PerformanceMetricRunFacet.json"
