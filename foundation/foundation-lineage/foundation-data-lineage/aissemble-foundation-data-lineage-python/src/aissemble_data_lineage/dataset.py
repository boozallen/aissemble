###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from openlineage.client.run import (
    Dataset as OpenLineageDataset,
    InputDataset as OpenLineageInputDataset,
    OutputDataset as OpenLineageOutputDataset,
)
from .lineage_base import _LineageBase, validate_facet_dict, get_open_lineage_facets
from .data_lineage_config import DataLineageConfig

"""
Represents basic Dataset entities in the context of Data Lineage.
"""


class Dataset(_LineageBase):
    _config: DataLineageConfig
    name: str = ""

    def __init__(self, name: str, facets=None):
        """
        Constructs a new Dataset object

        :param name(str): The name of the dataset.
        :param facets(dict): Collection of Facet objects describing this Dataset
        """
        super().__init__(facets)
        if not isinstance(name, str):
            raise ValueError(
                f"Illegal name object received.  Expected: str.  Received: {type(name)}"
            )
        self._config = DataLineageConfig()
        self.name = name

    def get_open_lineage_dataset(self) -> OpenLineageDataset:
        """
        Translates this generic Dataset object into an OpenLineage Dataset.

        :return A new OpenLineage Dataset object defined to be equivalent to this generic Dataset.
        """

        # TODO: Keeping as-is currently for consistency, but need to explore whether it's reasonable to be returning a
        # new instance every time versus treating this as a singleton wrapper.
        return OpenLineageDataset(
            name=self.name,
            namespace=self._config.dataset_namespace(self.name),
            facets=get_open_lineage_facets(self.facets),
        )


class InputDataset(Dataset):
    """
    Represents an input dataset in the context of Data Lineage.
    """

    input_facets: dict

    def __init__(self, name: str, facets=None, input_facets=None):
        """
        Constructs a new InputDataset

        :param name(str): The name of the dataset.
        :param facets(dict): Collection of general Facet objects describing this Dataset
        :param input_facets(dict): Collection of Facet objects describing the input of this Dataset.
        """
        super().__init__(name, facets)
        validate_facet_dict(input_facets)
        if input_facets is None:
            input_facets = {}
        self.input_facets = input_facets

    def get_open_lineage_dataset(self):
        return OpenLineageInputDataset(
            name=self.name,
            namespace=self._config.dataset_namespace(self.name),
            facets=get_open_lineage_facets(self.facets),
            inputFacets=get_open_lineage_facets(self.input_facets),
        )


class OutputDataset(Dataset):
    """
    Represents an output dataset in the context of Data Lineage.
    """

    name: str
    facets: dict
    output_facets: dict

    def __init__(self, name: str, facets=None, output_facets=None):
        """
        Constructs a new OutputDataset

        :param name(str): The name of the dataset.
        :param facets(dict): Collection of general Facet objects describing this Dataset
        :param output_facets(dict): Collection of Facet objects describing the output of this Dataset.
        """
        super().__init__(name, facets)
        validate_facet_dict(output_facets)
        if output_facets is None:
            self.output_facets = {}
        self.output_facets = output_facets

    def get_open_lineage_dataset(self):
        return OpenLineageOutputDataset(
            name=self.name,
            namespace=self._config.dataset_namespace(self.name),
            facets=get_open_lineage_facets(self.facets),
            outputFacets=get_open_lineage_facets(self.output_facets),
        )
