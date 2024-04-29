#!/bin/bash

###
# #%L
# aiSSEMBLE::Extensions::Docker::Quarkus
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
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
      export JAVA_OPTIONS="$JAVA_OPTIONS -DKRAUSENING_BASE=/deployments/krausening/"
    else
      export JAVA_OPTIONS="$JAVA_OPTIONS -DKRAUSENING_BASE=$KRAUSENING_BASE"
  fi
  if [[ ${KRAUSENING_EXTENSIONS:-x} == x ]]
    then
      echo "KRAUSENING_EXTENSIONS not set"
    else
      export JAVA_OPTIONS="$JAVA_OPTIONS -DKRAUSENING_EXTENSIONS=$KRAUSENING_EXTENSIONS"
  fi
  if [[ ${KRAUSENING_PASSWORD:-x} == x ]]
    then
      echo "KRAUSENING_PASSWORD not set"
    else
      export JAVA_OPTIONS="$JAVA_OPTIONS -DKRAUSENING_PASSWORD=$KRAUSENING_PASSWORD"
  fi
fi

export ENV_INITIALIZED=true
