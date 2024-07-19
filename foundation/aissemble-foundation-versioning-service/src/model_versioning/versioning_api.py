###
# #%L
# AIOps Docker Baseline::Versioning::Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from fastapi import FastAPI, Depends, HTTPException, status, Request
from pydantic import BaseModel
from typing import Optional
from model_versioning import version as model_versioning
from fastapi.security import (
    OAuth2PasswordBearer,
    HTTPBearer,
    HTTPAuthorizationCredentials,
)
from aissemble_security.pdp_client import PDPClient
from aissembleauth.auth_config import AuthConfig

app = FastAPI()


class VersioningResponse(BaseModel):
    success: bool
    message: str
    version: Optional[str] = None


class JWTBearer(HTTPBearer):
    def __init__(self, auto_error: bool = False):
        self.auth_config = AuthConfig()

        if self.auth_config.is_authorization_enabled():
            super(JWTBearer, self).__init__(auto_error=auto_error)

    async def __call__(self, request: Request):
        """
        This method is triggered when the REST endpoint is called.  Authorization status is checked in the
        config file (auth.properties) and if enabled we verify the jwt with the policy decision point.
        """
        token = ""
        if self.auth_config.is_authorization_enabled():
            credentials: HTTPAuthorizationCredentials = await super(
                JWTBearer, self
            ).__call__(request)

            if credentials:
                token = credentials.credentials
                pdp_client = PDPClient(self.auth_config.pdp_host_url())
                decision = pdp_client.authorize(token, "", "data-access")

                if "PERMIT" == decision:
                    print("User is authorized to run model versioning")
                else:
                    raise HTTPException(
                        status_code=status.HTTP_401_UNAUTHORIZED,
                        detail="User is not authorized",
                    )
            else:
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="No valid credentials found.",
                )

        return token


# jwt_bearer = JWTBearer()


@app.get("/")
def root(token: str = Depends(JWTBearer())):
    return "Versioning service is running"


@app.get("/version/model/{run_id}", response_model=VersioningResponse)
def read_item(run_id: str, token: str = Depends(JWTBearer())):
    try:
        version = model_versioning.version(run_id)
        message = "Successfully versioned model"
        success = True
    except Exception as e:
        version = None
        message = "Failed to version model: " + str(e)
        success = False

    return VersioningResponse(success=success, message=message, version=version)
