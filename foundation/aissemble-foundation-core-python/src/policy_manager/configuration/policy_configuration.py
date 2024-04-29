###
# #%L
# Policy-Based Configuration::Policy Manager (Python)
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from krausening.properties import PropertyManager


class PolicyConfiguration:
    """
    PolicyConfiguration is used to configure the policy location and defaults.
    """

    def __init__(self) -> None:
        self.properties = PropertyManager.get_instance().get_properties(
            "policy-configuration.properties"
        )

    def policiesLocation(self) -> str:
        """
        Configures the location and file name of the file that contains the
        policies.
        """
        return self.properties["policies-location"]
