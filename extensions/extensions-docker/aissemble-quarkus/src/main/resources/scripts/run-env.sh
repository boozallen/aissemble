#!/bin/bash

###
# #%L
# aiSSEMBLE::Extensions::Docker::Quarkus
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

# this file gets executed twice by run-java.sh
if [[ ${ENV_INITIALIZED:-false} == false ]]
then
  echo "Updating Quarkus Krausening environment variables..."
  if [[ ${KRAUSENING_BASE:-x} == x ]]
    then
      # Default to value that was originally in Dockerfile to maintain backwards compatability.
      echo "Defaulting KRAUSENING_BASE to /deployments/krausening/"
      export JAVA_OPTS_APPEND="$JAVA_OPTS_APPEND -DKRAUSENING_BASE=/deployments/krausening/"
    else
      export JAVA_OPTS_APPEND="$JAVA_OPTS_APPEND -DKRAUSENING_BASE=$KRAUSENING_BASE"
  fi
  if [[ ${KRAUSENING_EXTENSIONS:-x} == x ]]
    then
      echo "KRAUSENING_EXTENSIONS not set"
    else
      export JAVA_OPTS_APPEND="$JAVA_OPTS_APPEND -DKRAUSENING_EXTENSIONS=$KRAUSENING_EXTENSIONS"
  fi
  if [[ ${KRAUSENING_PASSWORD:-x} == x ]]
    then
      echo "KRAUSENING_PASSWORD not set"
    else
      export JAVA_OPTS_APPEND="$JAVA_OPTS_APPEND -DKRAUSENING_PASSWORD=$KRAUSENING_PASSWORD"
  fi
fi

export ENV_INITIALIZED=true
