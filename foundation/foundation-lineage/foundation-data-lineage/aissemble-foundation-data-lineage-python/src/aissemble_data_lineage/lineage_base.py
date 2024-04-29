###
# #%L
# aiSSEMBLE::Foundation::Data Lineage::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
"""
Contains base functionality common to various Data Lineage types.
"""
from typing import List, Dict

from .facet import Facet


def validate_facet_dict(facets: dict) -> None:
    """
    Convenience function for validating the schema of a provided facet dict.

    :param facets: Facet dict, which should conform to a dict of form str -> Facet
    :return: None
    """
    if facets is None:
        return
    if not isinstance(facets, dict):
        raise ValueError(
            f"Illegal facet collection received.  Expected: dict.  Received: {type(facets)}"
        )
    if not all([isinstance(key, str) for key in facets.keys()]) or not all(
        [isinstance(value, Facet) for value in facets.values()]
    ):
        raise ValueError(
            f"Illegal facet schema received.  Expected: dict of str -> Facet."
        )


def get_open_lineage_facets(facet_collection) -> Dict:
    """
    Converts generic Facet collection to OpenLineage object

    :return: Facet dict of form str -> OpenLineage BaseFacet
    """
    converted_dict = facet_collection.copy()
    for key in converted_dict.keys():
        converted_dict[key] = converted_dict[key].get_open_lineage_facet()
    return converted_dict


class _LineageBase:
    facets: dict[str, Facet]

    def __init__(self, facets: dict[str, Facet] = None):
        """
        Common supertype constructor for Runs, Jobs, and Datasets.

        :param facets(dict): Collection of Facet objects describing this Data Lineage element
        """
        self.set_facets(facets)

    def set_facets(self, facets: dict[str, Facet] = None):
        validate_facet_dict(facets)
        if facets is None:
            facets = {}
        self.facets = facets
