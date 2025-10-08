#!/bin/bash

###
# #%L
# aiSSEMBLE::Extensions::Docker::Hive Service
# %%
# Copyright (C) 2021 Booz Allen
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#      http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###


# DERIVED FROM apache/spark image entrypoint script
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0

set -x

DB_DRIVER=${DB_DRIVER:-derby}
if [[ $VERBOSE = "true" ]]; then
  VERBOSE_MODE="--verbose"
else
  VERBOSE_MODE=""
fi

function initialize_hive {
  COMMAND="-initOrUpgradeSchema"
  if [ "$(echo "$HIVE_VER" | cut -d '.' -f1)" -lt "4" ]; then
     COMMAND="-initSchema"
  fi
  # Don't honor verbose mode and dump errors because the 4.0.0 mysql schema generates a ton of deprecation warnings
  if "$HIVE_HOME/bin/schematool" -dbType $DB_DRIVER $COMMAND 2>/dev/null; then
    echo "Initialized schema successfully.."
  else
    echo "Schema initialization failed!"
    exit 1
  fi
}

export HIVE_CONF_DIR=$HIVE_HOME/conf
if [ -d "${HIVE_CUSTOM_CONF_DIR:-}" ]; then
  find "${HIVE_CUSTOM_CONF_DIR}" -type f -exec \
    ln -sfn {} "${HIVE_CONF_DIR}"/ \;
  export HADOOP_CONF_DIR=$HIVE_CONF_DIR
  export TEZ_CONF_DIR=$HIVE_CONF_DIR
fi

export HADOOP_CLIENT_OPTS="$HADOOP_CLIENT_OPTS -Xmx1G $SERVICE_OPTS"

if [ -z "$IS_RESUME" ]; then
  echo "Initializing (or upgrading) schema"
  initialize_hive
else
  echo "Skip schema initialization ($IS_RESUME)"
fi

export METASTORE_PORT=${METASTORE_PORT:-9083}
exec "$HIVE_HOME/bin/base" --skiphadoopversion $VERBOSE_MODE --service metastore
