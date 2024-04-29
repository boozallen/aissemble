###
# #%L
# aiSSEMBLE::Extensions::Docker::FastAPI
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###
from fastapi import FastAPI
app = FastAPI()
@app.get("/")
async def root():
    return {"message": "Congratulations! aiSSEMBLE FastAPI has been deployed! To overwrite the default application see docs here (https://github.com/boozallen/aissemble/tree/dev/extensions/extensions-helm/aissemble-fastapi-chart/src/main/resources/README.md)"}
