###
# #%L
# Drift Detection::Python Rest Client
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
import requests
from typing import List, Optional
from pydantic import BaseModel
from config.rest_config import RestConfig


class DriftData(BaseModel):
    """
    Represents the base class for drift data
    """

    name: Optional[str] = None


class DriftVariable(DriftData):
    """
    Represents a single drift variable
    """

    type = "single"
    value: float


class DriftVariables(DriftData):
    """
    Represents multiple drift variables
    """

    type = "multiple"
    variables: List[DriftVariable] = []


class DriftDataInput(BaseModel):
    """
    Represents the data needed.
    """

    input: Optional[DriftData]
    control: Optional[DriftData]


class DriftDetectionResult(BaseModel):
    """
    Represents the response from the invocation of drift
    """

    hasDrift: bool
    timestamp: str
    metadata: dict


class DriftRestClient:
    def __init__(self):
        self.config = RestConfig()

    def invoke_drift(self, policy_identifier, input, control):
        hostname = self.config.hostname()
        drift_data_input = DriftDataInput(input=input, control=control)
        query_params = {"policyIdentifier": policy_identifier}
        data_headers = {"Content-type": "application/json"}
        # uncomment below for debugging purposes
        # print('JSON being sent to drift invocation service: ' + drift_data_input.json())
        return requests.post(
            hostname + "/invoke-drift",
            params=query_params,
            data=drift_data_input.json(),
            headers=data_headers,
        )
