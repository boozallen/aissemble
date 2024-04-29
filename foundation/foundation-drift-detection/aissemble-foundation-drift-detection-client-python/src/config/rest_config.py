###
# #%L
# Drift Detection::Python Rest Client
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.properties import PropertyManager


class RestConfig:
    """
    Configurations utility class for being able to read in and reload the rest.properties file.
    """

    def __init__(self):
        self.properties = None
        self.reload()

    def hostname(self):
        return self.properties["drift.service.hostname"]

    def reload(self):
        self.properties = PropertyManager.get_instance().get_properties(
            "rest.properties"
        )
