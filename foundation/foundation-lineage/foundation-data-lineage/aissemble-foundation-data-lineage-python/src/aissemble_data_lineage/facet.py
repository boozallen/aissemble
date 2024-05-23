###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from openlineage.client.facet import BaseFacet as OpenLineageBaseFacet
from .data_lineage_config import DataLineageConfig
import attr

"""
Contains the general concepts needed to represent Facets in the context of Data Lineage
"""


class Facet:
    def __init__(self, schema_url: str):
        """
        Constructs a new Facet object

        :param schema_url(str): URI pointing to the applicable JSON schema representing the facet's structure.
        """
        _config = DataLineageConfig()
        if not isinstance(schema_url, str):
            raise ValueError(
                f"Illegal name object received.  Expected: str.  Received: {type(schema_url)}"
            )

        self.producer = _config.producer()
        self.schema_url = schema_url
        self._skip_attributes = {"producer", "schema_url", "_skip_attributes"}

    def get_open_lineage_facet(self) -> OpenLineageBaseFacet:
        """
        Translates this generic Facet object into an OpenLineage Facet.

        :return A new OpenLineage Facet object defined to be equivalent to this generic Facet.
        """
        mapping = {}
        attr_mapping = {}

        # Configure the mapping and attr_mapping dictionaries using the facet's attributes
        for key, value in self.__dict__.items():
            if key not in self._skip_attributes:
                mapping[key] = value
                attr_mapping[key] = attr.ib()

        # Create a new class that extends OpenLineage's BaseFacet with the facet's attributes
        # and apply the attr.s class decorator onto the class
        custom_lineage_class = type(
            self.__class__.__name__ + "OpenLineageFacet",
            (OpenLineageBaseFacet,),
            mapping,
        )
        # pylint: disable-next=not-callable
        custom_lineage_class = attr.s(these=attr_mapping, init=False)(
            custom_lineage_class
        )

        # Create the facet object using the new class
        facet = custom_lineage_class()

        facet._schemaURL = self.schema_url
        facet._producer = self.producer
        return facet


class _OpenLineageWrappedFacet(Facet):
    """
    Provides a mechanism of interfacing with Facets defined directly as descendants of the OpenLineage BaseFacet class.
    """

    def __init__(self, wrapped: OpenLineageBaseFacet):
        super().__init__(wrapped._get_schema())
        self.wrapped = wrapped

    def get_open_lineage_facet(self):
        # TODO: This represents deviant (but possibly more desirable) behavior-- We treat the wrapped facet
        # akin to a singleton.  Need to discuss whether we want to normalize to this behavior, or return a clone
        return self.wrapped


def from_open_lineage_facet(facet: OpenLineageBaseFacet) -> Facet:
    """
    Creates a new generic Facet from an existing OpenLineage Facet object.

    :param facet(OpenLineageBaseFacet) The OpenLineage Facet object to construct the new Facet from.

    :return A new generic Facet instance defined from the provided OpenLineage Facet.
    """
    return _OpenLineageWrappedFacet(facet)
