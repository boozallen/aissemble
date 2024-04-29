#!/usr/bin/env bash

###
# #%L
# AIOps Docker Baseline::FastAPI
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

if [ -z "$MODULE" ]; then
    echo "No module specified with the MODULE environment variable - defaulting to app directory"
elif [ ! -d /modules/$MODULE ]; then
    echo "Unable to start FastAPI application: /modules/$MODULE directory not found!"
    exit 1
else
    echo "Starting FastAPI application with module '$MODULE'..."
    # copy specified module to fastAPI app directory
    cp -r /modules/$MODULE/* /app
fi

# default values for starting the app
main=${API_MAIN:-'main'}
variable=${API_VARIABLE:-'app'}
host=${API_HOST:-'0.0.0.0'}
port=${API_PORT:-'80'}
if [ "$PROXY_HEADERS" = "true" ]; then
    proxyHeaders='--proxy-headers'
fi

uvicorn $main:$variable $proxyHeaders --host $host --port $port
