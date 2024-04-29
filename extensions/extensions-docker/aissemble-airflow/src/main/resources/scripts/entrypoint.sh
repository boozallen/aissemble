#!/bin/bash

###
# #%L
# AIOps Docker Baseline::Airflow
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

# shellcheck disable=SC1091

set -o errexit
set -o nounset
set -o pipefail
#set -o xtrace

# Start Airflow
echo "** Starting Airflow **"

# Default entrypoint for airflow server. Useful if running with Helm or Docker compose.
#exec /entrypoint "${@}"
# Execute the command
"${@}"
