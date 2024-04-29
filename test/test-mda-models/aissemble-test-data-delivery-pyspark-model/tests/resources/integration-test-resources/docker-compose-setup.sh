#!/bin/sh

###
# #%L
# aiSSEMBLE::Extensions::Encryption::Vault::Python
# %%
# Copyright (C) 2021 Booz Allen
# %%
# This software package is licensed under the Booz Allen Public License. All Rights Reserved.
# #L%
###

if ! command -v docker-compose &> /dev/null
then
  SYS_PLATFORM=$(uname -s | tr '[:upper:]' '[:lower:]')
  SYS_ARCH=$(uname -m | tr '[:upper:]' '[:lower:]')
  if 'arm64' == "$SYS_ARCH"
  then
    SYS_ARCH='aarch64'
  fi
  DOCKER_COMPOSE_RELEASE_URL="https://github.com/docker/compose/releases/download/v2.12.2/docker-compose-${SYS_PLATFORM}-${SYS_ARCH}"
  echo "Installing docker-compose v2.12.2 (${SYS_PLATFORM} ${SYS_ARCH})"
  echo "Pulling binary from ${DOCKER_COMPOSE_RELEASE_URL}..."
  curl -SL "${DOCKER_COMPOSE_RELEASE_URL}" -o /usr/local/bin/docker-compose
  chmod +x /usr/local/bin/docker-compose
else
  echo "Already installed"
fi
